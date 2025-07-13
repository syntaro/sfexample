package fsexample.fsexample.watchable.generic;


import fsexample.fsexample.namedvalue.NamedValueList;

public class MXWatchableBooleanYesNo extends MXWatchableBoolean {
    public MXWatchableBooleanYesNo(int name) {
        super(name);
    }
    @Override
    protected NamedValueList<Boolean> createNameTable() {
        NamedValueList<Boolean> nameTable = new NamedValueList<>();
        nameTable.addNameAndValue("No", false);
        nameTable.addNameAndValue("Yes", true);
        return nameTable;
    }
}
