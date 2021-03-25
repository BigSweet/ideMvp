package action;

import com.esotericsoftware.minlog.Log;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.project.Project;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class FreebrioAction extends AnAction {
    String mModuleName;//main
    Project project;
    String folderName;//路径
    String packagename;//不同moudle的packagename
    String currentAppName;//不同moudle的packagename

    @Override
    public void actionPerformed(AnActionEvent actionEvent) {
        project = actionEvent.getData(PlatformDataKeys.PROJECT);
        String fileName = actionEvent.getData(PlatformDataKeys.PSI_FILE).getName();
        folderName = actionEvent.getData(PlatformDataKeys.PSI_FILE).getContainingDirectory().toString().replace("PsiDirectory:", "");
        Log.debug(folderName);
        ActionEventContext actionEventContext = new ActionEventContext(actionEvent);
//        ActionEventContext actionEventContext = new ActionEventContext(actionEvent);
//        event.getTriggerDir().getFileSystem().findFileByPath(event.getTriggerDir().getPath().substring())
//        action.ActionEventContext actionEventContext = new action.ActionEventContext(actionEvent);
        packagename = actionEventContext.getTriggerPackageName();


        if (packagename.indexOf("freebrio") > 0) {
            //app包名
            currentAppName = "freebrio";
        } else {
            currentAppName = "freebeat";
        }

        if (fileName.contains(".java")) {
            mModuleName = fileName.substring(0, fileName.indexOf("Activity.java"));
        } else {
            mModuleName = fileName.substring(0, fileName.indexOf("Activity.kt"));
        }
        if (fileName.contains(".java")) {
            //contract
            String contratContent = ReadTemplateFile("/TemplateContract.txt");
            contratContent = dealTemplateContent(contratContent);
            writeToFile(contratContent, mModuleName + "Contract.java");
        } else {
            //contract
            String contratContent = ReadTemplateFile("/TemplateKtContract.txt");
            contratContent = dealTemplateContent(contratContent);
            writeToFile(contratContent, mModuleName + "Contract.kt");
        }

        if (fileName.contains(".java")) {
            //Presenter
            String presenterContent = ReadTemplateFile("/TemplatePresenter.txt");
            presenterContent = dealTemplateContent(presenterContent);
            writeToFile(presenterContent, mModuleName + "Presenter.java");
        } else {
            //Presenter
            String presenterContent = ReadTemplateFile("/TemplateKtPresenter.txt");
            presenterContent = dealTemplateContent(presenterContent);
            writeToFile(presenterContent, mModuleName + "Presenter.kt");
        }
        if (fileName.contains(".java")) {
            //ViewModel
            String viewModelContent = ReadTemplateFile("/TemplateViewModel.txt");
            viewModelContent = dealTemplateContent(viewModelContent);
            writeToFile(viewModelContent, mModuleName + "ViewModel.java");
        } else {
            //ViewModel
            String viewModelContent = ReadTemplateFile("/TemplateKtViewModel.txt");
            viewModelContent = dealTemplateContent(viewModelContent);
            writeToFile(viewModelContent, mModuleName + "ViewModel.kt");
        }


    }

    /**
     * 替换模板中字符
     *
     * @param content
     * @return
     */
    private String dealTemplateContent(String content) {
        if (content.contains("$packagename")) {
            content = content.replace("$packagename", packagename + "." + mModuleName.toLowerCase() + "mvp");
        }

        if (content.contains("$packagecons")) {
            content = content.replace("$packagecons", packagename);
        }
        content = content.replace("$name", mModuleName);
        content = content.replace("$author", "");
        content = content.replace("$date", getDate());
        content = content.replace("$cname", currentAppName);
        return content;
    }


    /**
     * 获取当前时间
     *
     * @return
     */
    public String getDate() {
        Date currentTime = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy/MM/dd");
        String dateString = formatter.format(currentTime);
        return dateString;
    }


    /**
     * 生成
     *
     * @param content   类中的内容
     * @param className 类文件名称
     */
    private void writeToFile(String content, String className) {
        try {
            File floder = new File(folderName + "/" + mModuleName.toLowerCase() + "mvp");
            if (!floder.exists()) {
                floder.mkdirs();
            }

            File file = new File(floder.getAbsolutePath() + "/" + className);
            if (!file.exists()) {
                file.createNewFile();
            }

            FileWriter fw = new FileWriter(file.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(content);
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    private String ReadTemplateFile(String fileName) {
        InputStream in = null;
        in = this.getClass().getResourceAsStream(fileName);
        String content = "";
        try {
            content = new String(readStream(in));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return content;
    }

    private byte[] readStream(InputStream inputStream) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len = -1;
        try {
            while ((len = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, len);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            outputStream.close();
            inputStream.close();
        }

        return outputStream.toByteArray();
    }
}
