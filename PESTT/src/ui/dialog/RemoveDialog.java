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

import ui.display.views.StatusImages;
import domain.constants.Images;

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

		// Create the OK button and add a handler.
		Button yes = new Button(shell, SWT.PUSH);
		StatusImages images = new StatusImages();
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

		// Create the cancel button and add a handler.
		Button cancel = new Button(shell, SWT.PUSH);
		Image cancelImage = images.getImage().get(Images.FAIL);
		cancel.setImage(cancelImage);
		cancel.setText("No");
		data = new GridData(GridData.FILL_HORIZONTAL);
		cancel.setLayoutData(data);
		cancel.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				input = null;
				shell.close();
			}
		});

		// Set the OK button as the default, so user can type input and press Enter to dismiss
		shell.setDefaultButton(yes);
	}
}