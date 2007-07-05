package simulation.db.commands;

import simulation.db.*;

/** Insert command.
 * @author ykk
 */
public class Insert
    implements SQLCommand
{
    //Members
    /** Name of table.
     */
    public String tableName;
    /** Values to insert.
     */
    public DBRow values;

    //Methods
    public String sqlQuery()
    {
	return "INSERT INTO "+tableName+
	    " VALUES ("+values.rowString()+");";
    }

    public int queryType()
    {
	return updateCmd;
    }

    public boolean run(Database db)
    {
	System.out.println(sqlQuery());
	SQLExecute execute = new SQLExecute(db, this);

	return true;
    }
}