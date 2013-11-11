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

import ui.StatusImages;
import ui.constants.Images;

public class RemoveDialog extends Dialog {

	private String message;
	private String input;

	public RemoveDialog(Shell parent, String message) {
		this(parent, SWT.DIALOG_TRIM | SWT.APPLICATION_MODAL, message);
	}

	public RemoveDialog(Shell parent, int style, String message) {
		super(parent, style);
		setText("Remove Dialog");
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
		Shell shell = new Shell(getParent(), getStyle()); // create the dialog
															// window.
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

		StatusImages images = new StatusImages();

		// Create the NO button and add a handler.
		Button no = new Button(shell, SWT.PUSH);
		Image cancelImage = images.getImage().get(Images.FAIL);
		no.setImage(cancelImage);
		no.setText("No");
		data = new GridData(GridData.FILL_HORIZONTAL);
		no.setLayoutData(data);
		no.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				input = null;
				shell.close();
			}
		});

		// Create the YES button and add a handler.
		Button yes = new Button(shell, SWT.PUSH);
		Image okImage = images.getImage().get(Images.PASS);
		yes.setImage(okImage);
		yes.setText("Yes");
		data = new GridData(GridData.FILL_HORIZONTAL);
		yes.setLayoutData(data);
		yes.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				input = "";
				shell.close();
			}
		});

		// Set the OK button as the default, so user can type input and press
		// Enter to dismiss
		shell.setDefaultButton(yes);
	}
}