import android.content.Context;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import utils.GetItemFromSYSTEM;
import utils.LSpinner_helper;
import xzr.La.systemtoolbox.modules.java.LModule;
import xzr.La.systemtoolbox.ui.StandardCard;
import xzr.La.systemtoolbox.ui.views.LSpinner;
import xzr.La.systemtoolbox.utils.process.ShellUtil;

import java.util.ArrayList;
import java.util.List;

public class CpuBoost implements LModule {
    final String CPU_BOOST_PATH="/sys/devices/system/cpu/cpu_boost";
    final String CPU_PATH="/sys/devices/system/cpu/cpu";
    final String SCHED_BOOST_ON_INPUT="sched_boost_on_input";
    final String INPUT_BOOST_FREQ="input_boost_freq";
    final String INPUT_BOOST_MS="input_boost_ms";


    @Override
    public String classname() {
        return "cpua";
    }

    @Override
    public View init(Context context) {
        if(incompatible())
            return null;
        int cpunum=getcpunum();
        if(cpunum<0)
            return null;

        ArrayList<List<String>> cpus_freq_table=new ArrayList<>();
        for(int i=0;i<cpunum;i++){
            cpus_freq_table.add(gen_freq_table(i));
        }

        ArrayList<String> core_boost_freq=gen_boost_freq();
        if(core_boost_freq.size()<=0)
            return null;


        LinearLayout linearLayout=new LinearLayout(context);
        linearLayout.setOrientation(LinearLayout.VERTICAL);

        {
            TextView textView= StandardCard.title(context);
            textView.setText("CPU升频");
            linearLayout.addView(textView);
        }

        {
            TextView textView= StandardCard.subtitle(context);
            textView.setText("您可以在此处调整基于输入事件的CPU升频");
            linearLayout.addView(textView);
        }
        if(node_detect(gen_boost_node(INPUT_BOOST_FREQ)))
        for(int cpu=0;cpu<cpunum;cpu++){
            LSpinner lSpinner=new LSpinner(context,cpus_freq_table.get(cpu));
            lSpinner.setSelection(LSpinner_helper.label2NearestAbovePosition(cpus_freq_table.get(cpu),core_boost_freq.get(cpu)));
            int finalCpu = cpu;
            linearLayout.addView(new TextView(context){{
                setText("CPU"+ finalCpu+"升频频率(kHz)");
            }});
            linearLayout.addView(lSpinner);
            lSpinner.setOnItemClickListener(new LSpinner.OnItemClickListener() {
                @Override
                public void onClick(int i) {
                    core_boost_freq.set(finalCpu,lSpinner.getSelectedLabel());//似乎并没有用 数据都存在lspinner里了。。
                    ShellUtil.run("echo \""+finalCpu+":"+lSpinner.getSelectedLabel()+"\" > "+gen_boost_node(INPUT_BOOST_FREQ),true);
                }
            });
        }

        if(node_detect(gen_boost_node(INPUT_BOOST_MS))) {
            LinearLayout linearLayout2 = new LinearLayout(context);
            linearLayout2.addView(new TextView(context) {{
                setText("升频时长(ms)：");
            }});

            EditText boost_ms = new EditText(context);
            boost_ms.setWidth(200);
            boost_ms.setText(ShellUtil.run("cat " + gen_boost_node(INPUT_BOOST_MS), true));
            linearLayout2.addView(boost_ms);
            {
                ImageButton imageButton = StandardCard.edittext_image_button(context);
                imageButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ShellUtil.run("echo \"" + boost_ms.getText().toString() + "\" > " +gen_boost_node(INPUT_BOOST_MS), true);
                    }
                });
                linearLayout2.addView(imageButton);
            }
            linearLayout.addView(linearLayout2);
        }

        linearLayout.addView(new TextView(context){{
            setText("有输入事件时改变CPU负载方式：");
        }});

        if(node_detect(gen_boost_node(SCHED_BOOST_ON_INPUT))){
            LSpinner lSpinner;
            List<String> common=new ArrayList(){{
                add("小核优先负载");
                add("大核优先负载");
            }};

            List<String> advance=new ArrayList(){{
                add("小核优先负载");
                add("大核优先负载");
                add("大核优先负载（仅优先迁移的调度组）");
                add("小核优先负载+更激进的升频");
            }};
            final String advance_check_node="/dev/stune/schedtune.sched_boost_no_override";
            boolean advance_support=ShellUtil.run("if [ -f "+advance_check_node+" ]\nthen\necho true\nfi\n",true).equals("true");
            if(advance_support)
                lSpinner=new LSpinner(context,advance);
            else
                lSpinner=new LSpinner(context,common);

            try {
                lSpinner.setSelection(Integer.parseInt(ShellUtil.run("cat " + gen_boost_node(SCHED_BOOST_ON_INPUT), true)));
            }
            catch (Exception e){}

            lSpinner.setOnItemClickListener(new LSpinner.OnItemClickListener() {
                @Override
                public void onClick(int i) {
                    ShellUtil.run("echo "+lSpinner.getSelection()+" > "+gen_boost_node(SCHED_BOOST_ON_INPUT),true);
                }
            });
            linearLayout.addView(lSpinner);
        }

        return linearLayout;
    }

    String gen_boost_node(String nodename){
        return CPU_BOOST_PATH+"/"+nodename;
    }

    ArrayList<String> gen_boost_freq(){
        String raw=ShellUtil.run("cat "+gen_boost_node(INPUT_BOOST_FREQ),true);
        List<String> per_core=GetItemFromSYSTEM.getall(raw);
        ArrayList<String> boost_freq=new ArrayList<>();
        //每一个核心:频率数据
        for(String per_core_str:per_core){
            String freq="NULL";
            //一个一个字的解析数据
            for(int i=0;i<per_core_str.length();i++){
                String c=per_core_str.substring(i,i+1);
                if(c.equals(":")) {
                    freq = per_core_str.substring(i + 1);
                    break;
                }
            }
            boost_freq.add(freq);
        }
        return boost_freq;
    }

    boolean node_detect(String node){
        if(ShellUtil.run("if [ -e "+node+" ]\n" +
                "then\n" +
                "echo true\n" +
                "fi",true).equals("true"))
            return true;
        return false;
    }

    List<String> gen_freq_table(int cpu){
        String raw=ShellUtil.run("cat "+CPU_PATH+cpu+"/cpufreq/scaling_available_frequencies",true);
        return GetItemFromSYSTEM.getall(raw);
    }

    int getcpunum(){
        String ret=ShellUtil.run("ls /sys/devices/system/cpu | grep -o \"cpu[0-9]*$\" | wc -l",true);
        int cpunum=-1;
        try{
            cpunum=Integer.parseInt(ret);
        }catch (Exception e){}

        return cpunum;
    }

    boolean incompatible(){
        if(!ShellUtil.run("if [ -e "+CPU_BOOST_PATH+" ]\n" +
                "then\n" +
                "echo true\n" +
                "fi\n",true).equals("true")){
            return true;
        }
        return false;
    }

    String write_cmd(String path,String value){
        return "echo \""+value+"\" > "+path;
    }

    @Override
    public String onBootApply() {
        String cmd="";
        String input_boost_freq=ShellUtil.run("cat "+gen_boost_node(INPUT_BOOST_FREQ),true);
        input_boost_freq=input_boost_freq.substring(0,input_boost_freq.length()-1);
        if(node_detect(gen_boost_node(INPUT_BOOST_FREQ)))
            cmd+=write_cmd(gen_boost_node(INPUT_BOOST_FREQ),input_boost_freq)+"\n";

        if(node_detect(gen_boost_node(INPUT_BOOST_MS)))
            cmd+=write_cmd(gen_boost_node(INPUT_BOOST_MS),ShellUtil.run("cat "+gen_boost_node(INPUT_BOOST_MS),true))+"\n";

        if(node_detect(gen_boost_node(SCHED_BOOST_ON_INPUT)))
            cmd+=write_cmd(gen_boost_node(SCHED_BOOST_ON_INPUT),ShellUtil.run("cat "+gen_boost_node(SCHED_BOOST_ON_INPUT),true))+"\n";
        return cmd;
    }

    @Override
    public void onExit() {

    }
}
