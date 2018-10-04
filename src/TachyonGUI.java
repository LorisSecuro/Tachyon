import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;

public class TachyonGUI {

	public static void main(String[] args) {
		final Display display = new Display();

		final Shell mainContainer = new Shell(display);
		final GridLayout mainLayout = new GridLayout(3, false);
		mainContainer.setLayout(mainLayout);
		mainContainer.setText("Tachyon");
		mainContainer.setSize(700, 200);

		// url
		final Label urlLabel = new Label(mainContainer, SWT.NONE);
		final GridData urlLabelLayoutData = new GridData(SWT.LEFT, SWT.CENTER, false, false);
		urlLabel.setLayoutData(urlLabelLayoutData);
		urlLabel.setText("URL:");

		final Text urlWidget = new Text(mainContainer, SWT.BORDER);
		final GridData urlWidgetLayoutData = new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1);
		urlWidget.setLayoutData(urlWidgetLayoutData);

		// output
		final Label outputLabel = new Label(mainContainer, SWT.NONE);
		final GridData outputLabelLayoutData = new GridData(SWT.LEFT, SWT.CENTER, false, false);
		outputLabel.setLayoutData(outputLabelLayoutData);
		outputLabel.setText("Output:");

		final Text outputWidget = new Text(mainContainer, SWT.BORDER);
		final GridData outputWidgetLayoutData = new GridData(SWT.FILL, SWT.CENTER, true, false);
		outputWidget.setLayoutData(outputWidgetLayoutData);

		final Button outputSelectWidget = new Button(mainContainer, SWT.PUSH);
		final GridData outputSelectWidgetLayoutData = new GridData(SWT.RIGHT, SWT.CENTER, false, false);
		outputSelectWidget.setLayoutData(outputSelectWidgetLayoutData);
		outputSelectWidget.setText("Browse");
		outputSelectWidget.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				final DirectoryDialog outputPathSelectWidget = new DirectoryDialog(mainContainer);
				final String path = outputPathSelectWidget.open();
				if (path != null) {
					outputWidget.setText(path);
				}
			}
		});

		// max connections
		final Label maxConnectionsLabel = new Label(mainContainer, SWT.NONE);
		final GridData maxConnectionsLabelLayoutData = new GridData(SWT.LEFT, SWT.CENTER, false, false);
		maxConnectionsLabel.setLayoutData(maxConnectionsLabelLayoutData);
		maxConnectionsLabel.setText("Max connections:");

		final Spinner maxConnectionsWidget = new Spinner(mainContainer, SWT.BORDER);
		final GridData maxConnectionsWidgetLayoutData = new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1);
		maxConnectionsWidget.setLayoutData(maxConnectionsWidgetLayoutData);
		maxConnectionsWidget.setValues(4, 1, 100, 0, 1, 4);

		// download
		final Button downloadSelectWidget = new Button(mainContainer, SWT.PUSH);
		final GridData downloadSelectWidgetLayoutData = new GridData(SWT.CENTER, SWT.CENTER, false, false, 3, 1);
		downloadSelectWidget.setLayoutData(downloadSelectWidgetLayoutData);
		downloadSelectWidget.setText("Download");
		downloadSelectWidget.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {

				final String url = urlWidget.getText();
				final String output = outputWidget.getText();
				final int maxConnections = maxConnectionsWidget.getSelection();

				try {
					new TachyonDownload(url, output, maxConnections);
				} catch (final Exception e1) {
					e1.printStackTrace();
				}
			}
		});

		mainContainer.open();

		while (!mainContainer.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		display.dispose();

	}

}
