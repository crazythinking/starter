package net.engining.drools.autoconfigure.props;

/**
 * @author Eric Lu
 */
public class DroolsProperties {

    /**
     * 规则文件目录，Kjar所在目录
     */
    private String path;

    /**
     * KieBase运行的模式：
     * <li>stream: 可以控制时间属性的运行模式，如Fact到达时不会立即运行规则，而是根据时间窗口或延迟控制执行时机</li>
     * <li>cloud: 传统运行模式，Fact到达时立即运行规则</li>
     */
    private String mode;

    /**
     * KieSession pool initial size
     */
    private int poolSize = 10;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public int getPoolSize() {
        return poolSize;
    }

    public void setPoolSize(int poolSize) {
        this.poolSize = poolSize;
    }
}
