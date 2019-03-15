package com.workflow.flowable.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

/**
 * @author chen.xing<br>
 * @version 1.0<br>
 * @Desc good study day day up
 * @data 2019<br>
 */
@SpringBootApplication(scanBasePackages = "com.workflow.flowable.server.configuration")
public class WorkflowApplication {
    private static final Logger logger = LoggerFactory.getLogger(WorkflowApplication.class);

    public static void main(String[] args) {
        new SpringApplicationBuilder()
                .sources(WorkflowApplication.class)
                .banner((environment, sourceClass, out) -> logger.error(
                        "\n" +
                                "                   _ooOoo_" + "\n" +
                                "                  o8888888o" + "\n" +
                                "                  88\" . \"88" + "\n" +
                                "                 (| -_- |)" + "\n" +
                                "                  O\\  =  /O" + "\n" +
                                "               ____/`---'\\____" + "\n" +
                                "             .'  \\\\|     |//  `." + "\n" +
                                "            /  \\\\|||  :  |||//  \\" + "\n" +
                                "           /  _||||| -:- |||||-  \\" + "\n" +
                                "           |   | \\\\\\  -  /// |   |" + "\n" +
                                "           | \\_|  ''\\---/''  |   |" + "\n" +
                                "           \\  .-\\__  `-`  ___/-. /" + "\n" +
                                "         ___`. .'  /--.--\\  `. . __" + "\n" +
                                "      .\"\" '<  `.___\\_<|>_/___.'  >'\"\"." + "\n" +
                                "     | | :  `- \\`.;`\\ _ /`;.`/ - ` : | |" + "\n" +
                                "     \\  \\ `-.   \\_ __\\ /__ _/   .-` /  /" + "\n" +
                                "======`-.____`-.___\\_____/___.-`____.-'======" + "\n" +
                                "                   `=---='" + "\n" +
                                "^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^" + "\n" +
                                "         佛祖保佑       永无BUG"))
                .run(args);
    }

}



