import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;

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

public class TachyonGUI extends Composite {

	public static void main(String[] args) {
		final Display display = new Display();

		final Shell shell = new Shell(display);
		final FillLayout shellLayout = new FillLayout();
		shell.setLayout(shellLayout);
		shell.setText("Tachyon");
		shell.setSize(700, 200);

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

		final GridLayout mainLayout = new GridLayout(3, false);
		setLayout(mainLayout);

		// url
		final Label urlLabel = new Label(this, SWT.NONE);
		final GridData urlLabelLayoutData = new GridData(SWT.LEFT, SWT.CENTER, false, false);
		urlLabel.setLayoutData(urlLabelLayoutData);
		urlLabel.setText("URL:");

		final Text urlWidget = new Text(this, SWT.BORDER);
		final GridData urlWidgetLayoutData = new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1);
		urlWidget.setLayoutData(urlWidgetLayoutData);

		// output
		final Label outputLabel = new Label(this, SWT.NONE);
		final GridData outputLabelLayoutData = new GridData(SWT.LEFT, SWT.CENTER, false, false);
		outputLabel.setLayoutData(outputLabelLayoutData);
		outputLabel.setText("Output:");

		final Text outputWidget = new Text(this, SWT.BORDER);
		final GridData outputWidgetLayoutData = new GridData(SWT.FILL, SWT.CENTER, true, false);
		outputWidget.setLayoutData(outputWidgetLayoutData);

		final Button outputSelectWidget = new Button(this, SWT.PUSH);
		final GridData outputSelectWidgetLayoutData = new GridData(SWT.RIGHT, SWT.CENTER, false, false);
		outputSelectWidget.setLayoutData(outputSelectWidgetLayoutData);
		outputSelectWidget.setText("Browse");
		outputSelectWidget.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				final DirectoryDialog outputPathSelectWidget = new DirectoryDialog(getShell());
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

		final Spinner maxConnectionsWidget = new Spinner(this, SWT.BORDER);
		final GridData maxConnectionsWidgetLayoutData = new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1);
		maxConnectionsWidget.setLayoutData(maxConnectionsWidgetLayoutData);
		maxConnectionsWidget.setValues(4, 1, 100, 0, 1, 4);

		// download
		final Button downloadSelectWidget = new Button(this, SWT.PUSH);
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
					final MessageBox dialog = new MessageBox(getShell(), SWT.ICON_ERROR | SWT.OK);
					dialog.setText("Error");
					dialog.setMessage(errorMessage);
					dialog.open();
					return;
				}

				// start the download
				BusyIndicator.showWhile(getDisplay(), new Runnable() {
					@Override
					public void run() {
						try {
							new TachyonDownload(url, output, maxConnections);
						} catch (final Exception e) {
							// error message
							final StringWriter sw = new StringWriter();
							final PrintWriter pw = new PrintWriter(sw);
							e.printStackTrace(pw);
							final String sStackTrace = sw.toString(); // stack trace as a string

							final MessageBox dialog = new MessageBox(getShell(), SWT.ICON_ERROR | SWT.OK);
							dialog.setText("Download error");
							dialog.setMessage(sStackTrace);
							dialog.open();
						}

					}
				});
			}
		});
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

}
