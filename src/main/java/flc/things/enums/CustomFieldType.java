package flc.things.enums;

import lombok.Getter;

import java.util.Objects;

@Getter
public enum CustomFieldType {
    TEXT("TEXT", "文本"),
    NUMBER("NUMBER", "数字"),
    DATA_DICT("DATA_DICT", "数据字典"),
    CODE("CODE", "代码");

    private final String code;
    private final String description;

    CustomFieldType(String code, String description) {
        this.code = code;
        this.description = description;
    }

    // 根据code获取CustomFieldType的方法
    public static CustomFieldType fromCode(String code) {
        for (CustomFieldType type : CustomFieldType.values()) {
            if (Objects.equals(type.getCode(), code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("No matching constant for [" + code + "]");
    }
}
