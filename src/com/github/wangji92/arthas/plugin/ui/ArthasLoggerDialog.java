package com.github.wangji92.arthas.plugin.ui;

import com.github.wangji92.arthas.plugin.utils.ClipboardUtils;
import com.github.wangji92.arthas.plugin.utils.NotifyUtils;
import com.google.common.collect.Lists;
import com.intellij.icons.AllIcons;
import com.intellij.ide.BrowserUtil;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.WindowManager;
import com.intellij.ui.components.labels.ActionLink;
import com.intellij.ui.components.labels.LinkLabel;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import java.awt.event.*;
import java.util.List;

/**
 * 动态更新日志等级
 * logger --name sample.mybatis.SampleXmlApplication --l warn
 */
public class ArthasLoggerDialog extends JDialog {
    private JPanel contentPane;
    private JButton updateLevelButton;
    private JTextField loggerExpressionEditor;
    private JButton scCommandButton;
    private JTextField classloaderHashEditor;
    private JComboBox logLevelComboBox;
    private JButton closeButton;
    private LinkLabel helpLink;
    private LinkLabel loggerBestLink;

    private String loggerExpression;

    private Project project;

    private String className;

    public ArthasLoggerDialog(Project project, String className) {
        this.project = project;
        this.className = className;
        setContentPane(contentPane);
        setModal(true);
        getRootPane().setDefaultButton(closeButton);

        // call onCancel() when cross is clicked
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

        // call onCancel() on ESCAPE
        contentPane.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
        init();
    }

    private void onOK() {
        // add your code here
        List<String> commands = Lists.newArrayList();
        commands.add(this.loggerExpression);

        //更新level
        String currentLoggerLevel = (String) logLevelComboBox.getSelectedItem();
        if (StringUtils.isNotBlank(currentLoggerLevel)) {
            commands.add("--level");
            commands.add(currentLoggerLevel);
        }

        String hashClassloader = classloaderHashEditor.getText();
        if (StringUtils.isNotBlank(hashClassloader)) {
            commands.add("-c");
            commands.add(hashClassloader);
        }
        String joinCommands = String.join(" ", commands);
        ClipboardUtils.setClipboardString(joinCommands);
        NotifyUtils.notifyMessage(project, "logger日志等级trace>debug>warn>info>error,-c hash值,--l 动态更新level");
    }

    private void onCancel() {
        // add your code here if necessary
        dispose();
    }

    /**
     * 打开窗口
     */
    public void open(String title) {
        setTitle(title);
        pack();
        //两个屏幕处理出现问题，跳到主屏幕去了
        setLocationRelativeTo(WindowManager.getInstance().getFrame(this.project));
        setVisible(true);
    }

    private void init() {
        this.scCommandButton.addActionListener(e -> getLoggerClassHashLoader());
        this.updateLevelButton.addActionListener(e -> onOK());
        this.closeButton.addActionListener(e -> onCancel());
        String loggerEx = String.join(" ", "logger", "--name", this.className);
        this.loggerExpressionEditor.setText(loggerEx);
        this.loggerExpression = loggerEx;
    }

    private void getLoggerClassHashLoader() {
        ClipboardUtils.setClipboardString(this.loggerExpression);
        NotifyUtils.notifyMessageDefault(project);
    }

    private void createUIComponents() {
        loggerBestLink = new ActionLink("", AllIcons.Ide.Link, new AnAction() {
            @Override
            public void actionPerformed(AnActionEvent anActionEvent) {
                BrowserUtil.browse("https://github.com/WangJi92/arthas-idea-plugin/issues/7");
            }
        });
        loggerBestLink.setPaintUnderline(false);

        helpLink = new ActionLink("", AllIcons.Ide.Link, new AnAction() {
            @Override
            public void actionPerformed(AnActionEvent anActionEvent) {
                BrowserUtil.browse("https://alibaba.github.io/arthas/logger.html");
            }
        });
        helpLink.setPaintUnderline(false);
    }
}
