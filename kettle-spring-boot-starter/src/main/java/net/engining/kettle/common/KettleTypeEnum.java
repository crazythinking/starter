package net.engining.kettle.common;

import net.engining.pg.support.enums.BaseEnum;
import net.engining.pg.support.meta.EnumInfo;

/**
 * Kettle 类型
 *
 * @author 陈宝
 * @version 1.0
 * @date 2020/9/23 18:21
 * @since 1.0
 */
@EnumInfo({
        "FILE_KTR|ktr 文件",
        "FILE_KTR|kjb job 文件",
        "FILE_KTR|repo 文件夹"
})
public enum KettleTypeEnum implements BaseEnum<String> {
    /**
     * ktr 文件
     */
    FILE_KTR("FILE_KTR","ktr 文件"),
    /**
     * kjb job 文件
     */
    FILE_KJB("FILE_KTR","kjb job 文件"),
    /**
     * repo 文件夹
     */
    FILE_REPO("FILE_KTR","repo 文件夹");

    private String value;

    private String label;

    public void setLabel(String label) {
        this.label = label;
    }

    KettleTypeEnum(String value, String label) {
        this.value = value;
        this.label=label;
    }

    @Override
    public String getValue() {
        return value;
    }

    @Override
    public String getLabel() {
        return null;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
