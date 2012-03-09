package ui.dialog;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import ui.StatusImages;
import ui.constants.Images;

public class InputDialog extends Dialog {
	
	private String message;
	private String input;

	public InputDialog(Shell parent, String message) {
		this(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL, message);
	}

	public InputDialog(Shell parent, int style, String message) {
		super(parent, style);
		setText("Input Dialog");
		setMessage(message);
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getInput() {
		return input;
	}

	public void setInput(String input) {
		this.input = input;
	}

	public String open() {
		Shell shell = new Shell(getParent(), getStyle()); // create the dialog window.
		shell.setText(getText());
		createContents(shell);
		shell.pack();
		shell.open();
		Display display = getParent().getDisplay();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		return input;
	}

	private void createContents(final Shell shell) {
		shell.setLayout(new GridLayout(2, true));
		Label label = new Label(shell, SWT.NONE);
		label.setText(message);
		GridData data = new GridData();
		data.horizontalSpan = 2;
		label.setLayoutData(data);

		final Text text = new Text(shell, SWT.BORDER); // Display the input box.
		data = new GridData(GridData.FILL_HORIZONTAL);
		data.horizontalSpan = 2;
		text.setLayoutData(data);

		// Create the OK button and add a handler.
		Button ok = new Button(shell, SWT.PUSH);
		StatusImages images = new StatusImages();
		Image okImage = images.getImage().get(Images.PASS);
		ok.setImage(okImage);
		ok.setText("OK");
		data = new GridData(GridData.FILL_HORIZONTAL);
		ok.setLayoutData(data);
		ok.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				input = text.getText();
				shell.close();
			}
		});

		// Create the cancel button and add a handler.
		Button cancel = new Button(shell, SWT.PUSH);
		Image cancelImage = images.getImage().get(Images.FAIL);
		cancel.setImage(cancelImage);
		cancel.setText("Cancel");
		data = new GridData(GridData.FILL_HORIZONTAL);
		cancel.setLayoutData(data);
		cancel.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				input = null;
				shell.close();
			}
		});

		// Set the OK button as the default, so user can type input and press Enter to dismiss
		shell.setDefaultButton(ok);
	}
}