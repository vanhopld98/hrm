package vn.com.humanresourcesmanagement.common.enums;


import java.util.List;

public enum RoleEnum {

    USER(new String[]{"ROLE_USER"}),
    ADMIN(new String[]{"ROLE_ADMIN"}),
    NON(new String[]{}),
    ALL(new String[]{"ROLE_USER", "ROLE_ADMIN"});

    private final String[] values;

    RoleEnum(String[] values) {
        this.values = values;
    }

    public String[] getValues() {
        return values;
    }

    public List<String> getValueAsList() {
        return List.of(values);
    }

}
