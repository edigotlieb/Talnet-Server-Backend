/**
 * FILE : Colomb.java AUTHORS : Erez Gotlieb DESCRIPTION :
 */
package Request.AppRequest;

public class Column {

	public enum COL_TYPE {
		INT, VARCHAR, TIMESTAMP, DATE
	}
	private final String colName;
	public final int defaultVarcharSize = 50;

	public String getColName() {
		return colName;
	}
	private final COL_TYPE type;
	private final int size; // -1 for default
	boolean autoInc = false;
	boolean isPrimary = false;

	public boolean isPrimary() {
		return isPrimary;
	}

	public Column(String colName, COL_TYPE dataType, int size) {
		this.colName = colName;
		this.type = dataType;
		this.size = size;
	}

	public Column(String colName, COL_TYPE dataType, int size, boolean autoInc, boolean isPrimary) {
		this(colName, dataType, size);
		this.autoInc = autoInc;
		this.isPrimary = isPrimary;
	}

	@Override
	public String toString() {
		String col = colName + " " + type;
		if (type != COL_TYPE.DATE && type != COL_TYPE.TIMESTAMP && size >= 0) {
			col += "(" + size + ")";
		} else if(type == COL_TYPE.VARCHAR && size < 0){
			col += "(" + defaultVarcharSize + ")";
		}
		if (this.autoInc) {
			col += " AUTO_INCREMENT";
		}
		return col;
	}
}
