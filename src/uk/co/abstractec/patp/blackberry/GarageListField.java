package uk.co.abstractec.patp.blackberry;

import net.rim.device.api.ui.DrawStyle;
import net.rim.device.api.ui.Field;
import net.rim.device.api.ui.Font;
import net.rim.device.api.ui.Graphics;
import net.rim.device.api.ui.Manager;
import net.rim.device.api.ui.component.ListField;
import net.rim.device.api.ui.component.ListFieldCallback;

public class GarageListField extends ListField {

	protected GarageRowManager[] _rows;

	private int originalRowHeight;

	public GarageListField() {

		originalRowHeight = getRowHeight();
		setRowHeight(originalRowHeight * 2 + 10);
	}

	public Field getSelectedField() {
		return _rows[getSelectedIndex()].getField(0);
	}

	public Garage getSelectedGarage() {
		return _rows[getSelectedIndex()].getGarage();
	}

	private class GarageRowManager extends Manager {

		private Garage garage;

		public GarageRowManager(Garage garage) {
			super(0);
			this.garage = garage;
		}

		public Garage getGarage() {
			return garage;
		}

		public void drawRow(Graphics g, int x, int y, int width, int height) {

			Font smallFont = g.getFont().derive(Font.PLAIN, 15);

			Font boldFont = g.getFont().derive(Font.BOLD);
			Font bigBoldFont = g.getFont().derive(Font.BOLD, height - 20);

			int mainTextWidth = 270;

			g.setFont(boldFont);
			g.drawText(garage.getName(), x, y, DrawStyle.ELLIPSIS,
					mainTextWidth);

			g.setFont(smallFont);
			g.drawText(garage.getDescription(), x, y + originalRowHeight,
					DrawStyle.ELLIPSIS, mainTextWidth);

			int milesX = x + mainTextWidth + 20;
			int milesQualifierX = x + mainTextWidth + 10;

			if (garage.getDistanceInMiles() >= 10) {
				milesX -= 10;
			}

			g.setFont(bigBoldFont);
			g.drawText("" + garage.getDistanceInMiles(), milesX, y);

			g.setFont(smallFont);
			String mileageQualifier = (garage.getDistanceInMiles() == 1 ? "mile"
					: "miles");
			g.drawText(mileageQualifier, milesQualifierX, y + originalRowHeight
					+ 10);

			// Arrange the cell fields within this row manager.
			layout(width, height);

			// Place this row manager within its enclosing list.
			setPosition(x, y);

			// Apply a translating/clipping transformation to the graphics
			// context so that this row paints in the right area.
			g.pushRegion(getExtent());

			// Paint this manager's controlled fields.
			subpaint(g);

			// Restore the graphics context.
			g.popContext();
		}

		// Arrages this manager's controlled fields from left to right within
		// the enclosing table's columns.
		protected void sublayout(int width, int height) {
			setExtent(getPreferredWidth(), getPreferredHeight());
		}

		// The preferred width of a row is defined by the list renderer.
		public int getPreferredWidth() {
			return RENDERER.getPreferredWidth(GarageListField.this);
		}

		// The preferred height of a row is the "row height" as defined in the
		// enclosing list.
		public int getPreferredHeight() {
			return getRowHeight();
		}
	}

	// The ListFieldCallback object that renders the rows of a TableListField.
	private static final ListFieldCallback RENDERER = new ListFieldCallback() {
		// A TableListField's row is drawn by delegating to the GarageRowManager
		// for that row.
		public void drawListRow(ListField listField, Graphics graphics,
				int index, int y, int width) {
			GarageListField garageListField = (GarageListField) listField;
			GarageRowManager rowManager = garageListField._rows[index];
			rowManager.drawRow(graphics, 0, y, width, garageListField
					.getRowHeight());
		}

		// The preferred width of a table list field is the sum of the widths of
		// its columns.
		public int getPreferredWidth(ListField listField) {
			return 0;
		}

		// there is no meaningful "row object"
		public Object get(ListField listField, int index) {
			return null;
		}

		// prefix searching is not supported
		public int indexOfList(ListField listField, String prefix, int start) {
			return -1;
		}
	};

	public void setGarages(Garage[] garages) {
		int numRows = garages.length;

		// Create a row manager for each row.
		_rows = new GarageRowManager[numRows];
		for (int curRow = 0; curRow < numRows; curRow++) {
			_rows[curRow] = new GarageRowManager(garages[curRow]);
		}
		// Store the layout data.

		// Configure this ListField to operate with TableListField semantics.
		setSize(numRows);
		setCallback(RENDERER);
		
	}
}
