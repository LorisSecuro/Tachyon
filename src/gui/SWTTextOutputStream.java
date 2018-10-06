package gui;

import java.io.IOException;
import java.io.OutputStream;

import org.eclipse.swt.widgets.Text;

public class SWTTextOutputStream extends OutputStream {

	private final Text text;

	public SWTTextOutputStream(Text text) {
		this.text = text;
	}

	@Override
	public void write(byte[] b, int off, int len) throws IOException {
		updateText(new String(b, off, len));
	}

	@Override
	public void write(int b) throws IOException {
		updateText(String.valueOf((char) b));
	}

	@Override
	public void write(byte[] b) throws IOException {
		write(b, 0, b.length);
	}

	private void updateText(String s) {
		if (text != null) {
			text.getDisplay().asyncExec(new Runnable() {
				@Override
				public void run() {
					if (!text.isDisposed()) {
						text.append(s);
					}
				}
			});
		}
	}

}
