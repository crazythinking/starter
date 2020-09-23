package net.engining.kettle.common;

/**
 * Kettle 类型
 *
 * @author 陈宝
 * @version 1.0
 * @date 2020/9/23 18:21
 * @since 1.0
 */
public enum KettleType {
    /**
     * ktr 文件
     */
    file_ktr("ktr 文件"),
    /**
     * kjb job 文件
     */
    file_kjb("kjb job 文件"),
    /**
     * repo 文件夹
     */
    file_repo("repo 文件夹");

    private String value;

    KettleType(String value){
        this.value = value;
    }
    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
