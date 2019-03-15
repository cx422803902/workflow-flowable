package org.flowable.cloud.rocketmq.properties;

/**
 * <Description> <br>
 *
 * @author chen.xing01<br>
 * @version 1.0<br>
 */
public class RocketMQProperties {

    private String groupName;
    private String nameServiceAddr;


    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getNameServiceAddr() {
        return nameServiceAddr;
    }

    public void setNameServiceAddr(String nameServiceAddr) {
        this.nameServiceAddr = nameServiceAddr;
    }

}
