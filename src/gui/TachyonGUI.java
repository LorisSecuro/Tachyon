package gui;

import java.io.File;
import java.io.PrintStream;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.BusyIndicator;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;

import tachyon.TachyonDownload;

public class TachyonGUI extends Composite {

	private Display display;
	private Shell shell;

	private Text urlWidget;
	private Text outputWidget;
	private Button outputSelectWidget;
	private Spinner maxConnectionsWidget;
	private Button downloadSelectWidget;
	private Text loggerWidget;

	public static void main(String[] args) {
		final Display display = new Display();

		final Shell shell = new Shell(display);
		final FillLayout shellLayout = new FillLayout();
		shell.setLayout(shellLayout);
		shell.setText("Tachyon");
		shell.setSize(700, 400);

		new TachyonGUI(shell, SWT.NONE);

		shell.open();

		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		display.dispose();
	}

	public TachyonGUI(Composite parent, int style) {
		super(parent, style);

		display = getDisplay();
		shell = getShell();

		final GridLayout mainLayout = new GridLayout(3, false);
		setLayout(mainLayout);

		// url
		final Label urlLabel = new Label(this, SWT.NONE);
		final GridData urlLabelLayoutData = new GridData(SWT.LEFT, SWT.CENTER, false, false);
		urlLabel.setLayoutData(urlLabelLayoutData);
		urlLabel.setText("URL:");

		urlWidget = new Text(this, SWT.BORDER);
		final GridData urlWidgetLayoutData = new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1);
		urlWidget.setLayoutData(urlWidgetLayoutData);

		// output
		final Label outputLabel = new Label(this, SWT.NONE);
		final GridData outputLabelLayoutData = new GridData(SWT.LEFT, SWT.CENTER, false, false);
		outputLabel.setLayoutData(outputLabelLayoutData);
		outputLabel.setText("Output:");

		outputWidget = new Text(this, SWT.BORDER);
		final GridData outputWidgetLayoutData = new GridData(SWT.FILL, SWT.CENTER, true, false);
		outputWidget.setLayoutData(outputWidgetLayoutData);

		outputSelectWidget = new Button(this, SWT.PUSH);
		final GridData outputSelectWidgetLayoutData = new GridData(SWT.RIGHT, SWT.CENTER, false, false);
		outputSelectWidget.setLayoutData(outputSelectWidgetLayoutData);
		outputSelectWidget.setText("Browse");
		outputSelectWidget.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				final DirectoryDialog outputPathSelectWidget = new DirectoryDialog(shell);
				final String path = outputPathSelectWidget.open();
				if (path != null) {

					// get the filename from the url to add to the selected folder
					final String url = urlWidget.getText();
					final String fileName = new File(url).getName();

					outputWidget.setText(path + File.separator + fileName);
				}
			}
		});

		// max connections
		final Label maxConnectionsLabel = new Label(this, SWT.NONE);
		final GridData maxConnectionsLabelLayoutData = new GridData(SWT.LEFT, SWT.CENTER, false, false);
		maxConnectionsLabel.setLayoutData(maxConnectionsLabelLayoutData);
		maxConnectionsLabel.setText("Max connections:");

		maxConnectionsWidget = new Spinner(this, SWT.BORDER);
		final GridData maxConnectionsWidgetLayoutData = new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1);
		maxConnectionsWidget.setLayoutData(maxConnectionsWidgetLayoutData);
		maxConnectionsWidget.setValues(4, 1, 100, 0, 1, 4);

		// download
		downloadSelectWidget = new Button(this, SWT.PUSH);
		final GridData downloadSelectWidgetLayoutData = new GridData(SWT.CENTER, SWT.CENTER, false, false, 3, 1);
		downloadSelectWidget.setLayoutData(downloadSelectWidgetLayoutData);
		downloadSelectWidget.setText("Download");
		downloadSelectWidget.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {

				final String url = urlWidget.getText();
				final String output = outputWidget.getText();
				final int maxConnections = maxConnectionsWidget.getSelection();

				// checks if the parameters are valid
				final String errorMessage = validityChecks(url, output, maxConnections);
				if (errorMessage != null) {
					final MessageBox dialog = new MessageBox(shell, SWT.ICON_ERROR | SWT.OK);
					dialog.setText("Error");
					dialog.setMessage(errorMessage);
					dialog.open();
					return;
				}

				// clear the logger
				loggerWidget.setText("");

				// disable UI widgets
				setEnabledWidgets(false);

				// to not freeze the UI, I run the download in a different thread, with the busy indicator.
				// the method used here is from:
				// http://git.eclipse.org/c/platform/eclipse.platform.swt.git/tree/examples/org.eclipse.swt.snippets/src/org/eclipse/swt/snippets/Snippet130.java 
				final Runnable downloadRunnable = new Runnable() {
					boolean done = false;

					@Override
					public void run() {
						final Thread thread = new Thread(() -> {

							try {
								new TachyonDownload(url, output, maxConnections);
							} catch (final Exception e) {
								e.printStackTrace();
							}

							done = true;
							display.wake();
						});
						thread.start();
						while (!done && !shell.isDisposed()) {
							if (!display.readAndDispatch())
								display.sleep();
						}
					}
				};
				BusyIndicator.showWhile(display, downloadRunnable);

				// enable UI widgets
				setEnabledWidgets(true);
			}
		});

		// logger
		loggerWidget = new Text(this, SWT.MULTI | SWT.BORDER | SWT.WRAP | SWT.V_SCROLL | SWT.READ_ONLY);
		final GridData loggerWidgetLayoutData = new GridData(SWT.FILL, SWT.FILL, true, true, 3, 1);
		loggerWidget.setLayoutData(loggerWidgetLayoutData);

		// set the print stream to the logging text
		final PrintStream loggerStream = new PrintStream(new SWTTextOutputStream(loggerWidget));
		System.setOut(loggerStream);
		System.setErr(loggerStream);

	}

	// checks if the parameters are valid, otherwise returns an error String
	private static String validityChecks(String url, String output, int maxConnections) {
		if (url.trim().isEmpty()) {
			return "URL is required.";
		}

		if (output.trim().isEmpty()) {
			return "Output is required.";
		}

		// same check that is in Tachyon.java
		final File file = new File(output);
		final File parentFile = file.getParentFile();
		if (parentFile == null || !(parentFile.isDirectory() && parentFile.exists())) {
			return "Output path is invalid.";
		}

		if (maxConnections <= 0) {
			return "Max connections must be greater than 0.";
		}

		return null;
	}

	// enable/disable the widgets
	private void setEnabledWidgets(boolean enabled) {
		urlWidget.setEnabled(enabled);
		outputWidget.setEnabled(enabled);
		outputSelectWidget.setEnabled(enabled);
		maxConnectionsWidget.setEnabled(enabled);
		downloadSelectWidget.setEnabled(enabled);
	}

}
