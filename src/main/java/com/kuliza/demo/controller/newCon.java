package com.kuliza.demo.controller;

import com.kuliza.demo.implementations.CheckRegex;
import com.kuliza.demo.model.*;
import com.kuliza.demo.repository.*;
import com.kuliza.demo.service.SendEmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;


@Controller
@EnableScheduling
public class newCon {
    userDetails userdetails=new userDetails();

    @Autowired
    private UserRepository repo;

    @Autowired
    private RiskRepository riskRepo;

    @Autowired
    private UserPolicyRepository policyRepo;

    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    private UserTestingLogsRepository userTestingLogsRepo;

    @Autowired
    private Risk_PolicyRepository risk_policyRepository;

    @Autowired
    private DynamicTestingLogsRepository dynamicTestingLogsRepository;

    @Autowired
    SendEmailService sendEmailService;

    String violatedPolicy = "";
    String violatedRiks = "";
    String getPolicyStatus;
    int cntDelete=0;
    int cntMove=0;
    File f;
    String movingDir="";
    String email;
    String name;


    public newCon() throws FileNotFoundException {
    }

    @RequestMapping("/check")
    public String checkForRisk( Model model,@AuthenticationPrincipal UserDetails ud) throws IOException {

        dynamicTestingLogsRepository.deleteAll();
        name=ud.getUsername();
        System.out.println("this is the current user while testing"+ud.getUsername());
        checkRisk();
        userdetails=repo.findByUser_name(ud.getUsername());
        email=userdetails.getUser_email();
        List<DynamicTestingLogs> list=dynamicTestingLogsRepository.fetcgDetails(ud.getUsername());
        model.addAttribute("getViolatedDetails",list);
        return "ShowDynamicResult";
    }

    @Scheduled(fixedDelay = 120000)
    public void checkRisk() throws IOException {
//        SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        dynamicTestingLogsRepository.deleteAll();
        CheckRegex cr = new CheckRegex();
        HashMap<String, Integer> map = new HashMap<>();
        System.out.println(System.getProperty("user.dir"));
        String dir=System.getProperty("user.dir")+"/uploads/"+name;
        File folder = new File(dir);
        File[] listOfFiles = folder.listFiles();
        if (listOfFiles.length > 0)
        {
            for (int j = 0; j < listOfFiles.length; j++) {
                System.out.println("This is the name of the file "+listOfFiles[j]);
                String str1 = "";
                violatedPolicy="";
                violatedRiks="";
                if (listOfFiles[j].isFile()) {

                    SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                    System.out.println("After Format : " + sdf.format(listOfFiles[j].lastModified()));


                    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
                    LocalDateTime now = LocalDateTime.now();
                    System.out.println(dtf.format(now));

                    boolean flag = findDifference(String.valueOf(sdf.format(listOfFiles[j].lastModified())), String.valueOf(dtf.format(now)));
                    System.out.println("Changes Made: "+flag);
                    if (flag == true) {

                        FileInputStream fis = new FileInputStream(listOfFiles[j]);
                        InputStreamReader isr = new InputStreamReader(fis, Charset.defaultCharset());
                        BufferedReader br = new BufferedReader(isr);
                        String line;
                        while ((line = br.readLine()) != null) {
                            str1 = str1 + line;
                        }
                        br.close();

                        System.out.println(".......");
                        System.out.println("Content of the testing document ");
                        List<String> words = Arrays.asList(str1.split(" "));
                        for (String s : words) System.out.println(s);
                        System.out.println(".......");

                        map.clear();
                        for (String s : words) {
                            map.put(s, map.getOrDefault(s, 0) + 1);
                        }
                        System.out.println(map);


                        List<UserPolicy> listPolicy = policyRepo.getEveryPolicy(name);

                        for (UserPolicy up1 : listPolicy) {
                            if(up1.isEnabled()==false)
                            {
                                System.out.println("False Policy");
                                continue;
                            }
                            List<Risk_Policy> listRiskPolicy = risk_policyRepository.findByPolicyName(up1.getPolicy_name());
                            List<Long> riskId = new ArrayList<>();
                            for (Risk_Policy rp : listRiskPolicy) {
                                riskId.add(rp.getRisk_id());
                            }
                            for (Long i : riskId) {
                                List<Risk_Details> getKeyWords = riskRepo.findAllKeyWords(i);
                                for (Risk_Details rd : getKeyWords) {
                                    int cnt1 = 0;
                                    String str = rd.getRisk_keyword();
                                    String strRegex = rd.getRisk_regex();
                                    List<String> riskKey = Arrays.asList(str.split(","));
                                    List<String> regexKey = Arrays.asList(strRegex.split(","));
                                    for (String s : riskKey) {
                                        if (s.length() == 0) continue;
                                        if (map.containsKey(s)) cnt1 += map.get(s);

                                    }
                                    for (String s : regexKey) {
                                        if (s.length() == 0) continue;
                                        cnt1 += cr.checkR(s, map);
                                    }

                                /*
                                    IF ANY RISK IS VIOLATED THEN THE POLICY IS ALSO VIOLATED.............................
                                 */
                                    if (cnt1 >= rd.getMatch_count()) {
                                        String remedyStatus=up1.getRemedy_type();

                                        if(remedyStatus.equals("Delete"))
                                        {
                                            cntDelete++;
                                        }
                                        else if(remedyStatus.equals("Move"))
                                        {
                                            cntMove++;
                                        }
                                        f=new File(String.valueOf(listOfFiles[j]));
                                        System.out.println("this is the file name "+f);
                                        getPolicyStatus= up1.getStatus();
                                        System.out.println("Risk With id " + rd.getRisk_id() + " exceed the match count thus policy with policy name " + up1.getPolicy_name() + " is violated");
                                        /*
                                                Mail Sending ........................
                                         */
//                                        String[] emailAdress=new String[2];
//                                        emailAdress[0]="thakur.anubhav007@gmail.com";
//                                        emailAdress[1]=email;
//                                        if(getPolicyStatus.equals("Admin"))
//                                        {
//                                            sendEmailService.sendEmail(emailAdress[0],"This policy is violated "+up1.getPolicy_name(),"Critical");
//                                        }
////
//                                        else if(getPolicyStatus.equals("User"))
//                                        {
//                                            sendEmailService.sendEmail(emailAdress[1],"This policy is violated "+up1.getPolicy_name(),"Critical");
//                                        }


                                        violatedPolicy = violatedPolicy + up1.getPolicy_name() + ",";
                                        violatedRiks = violatedRiks + rd.getRisk_id() + ",";
                                        UserTestingLogs utl = new UserTestingLogs();
                                        utl.setRisk_id(i + "");
                                        utl.setPolicy_name(up1.getPolicy_name());
                                        utl.setDate(LocalDate.now() + "");
                                        utl.setTime(LocalTime.now() + "");
                                        utl.setUser_name(name);
                                        userTestingLogsRepo.save(utl);
                                        DynamicTestingLogs d=new DynamicTestingLogs();
                                        d.setDate(LocalDate.now()+"");
                                        d.setTime(LocalTime.now()+"");
                                        d.setRisk_id(i);
                                        d.setPolicy_name(up1.getPolicy_name());
                                        d.setUser_name(name);
                                        dynamicTestingLogsRepository.save(d);
                                    } else
                                        System.out.println("No risk exceed the match count with risk id " + rd.getRisk_id());
                                }
                            }

                            System.out.println("Scanning for next policy...");
                            for (int i = 0; i < 4; i++) System.out.println(".");
                        }
                        System.out.println("No policies remaining");
                        if(cntDelete>=cntMove)
                        {
                            if(f.exists())
                            {
                                f.delete();
                            }
                        }
                        else
                        {
                                movingDir=System.getProperty("user.dir")+"/uploads/";
                                String p=""+f;
                                System.out.println("This is the new file name "+(p.length()-9));
                                Path temp= Files.move(Paths.get(""+f),Paths.get(movingDir+p.substring(p.length()-9)), StandardCopyOption.REPLACE_EXISTING);
                                System.out.println("Files Moved");
                        }
                    }
                }
                System.out.println("Scanning another File");
            }
        }
        System.out.println("Program Ended");
    }
    public boolean findDifference(String start_date,
                                  String end_date)
    {

        SimpleDateFormat sdf
                = new SimpleDateFormat(
                "dd-MM-yyyy HH:mm:ss");
        boolean flag=true;


        try {

            Date d1 = sdf.parse(start_date);
            Date d2 = sdf.parse(end_date);

            long difference_In_Time
                    = d2.getTime() - d1.getTime();

            long difference_In_Seconds
                    = (difference_In_Time
                    / 1000)
                    % 60;

            long difference_In_Minutes
                    = (difference_In_Time
                    / (1000 * 60))
                    % 60;

            long difference_In_Hours
                    = (difference_In_Time
                    / (1000 * 60 * 60))
                    % 24;

            long difference_In_Years
                    = (difference_In_Time
                    / (1000l * 60 * 60 * 24 * 365));

            long difference_In_Days
                    = (difference_In_Time
                    / (1000 * 60 * 60 * 24))
                    % 365;


            if(difference_In_Days>0||difference_In_Hours>0||difference_In_Years>0||difference_In_Minutes>2)
            {
                flag=false;
            }
            System.out.print(
                    "Difference "
                            + "between two dates is: ");

            System.out.println(
                    difference_In_Years
                            + " years, "
                            + difference_In_Days
                            + " days, "
                            + difference_In_Hours
                            + " hours, "
                            + difference_In_Minutes
                            + " minutes, "
                            + difference_In_Seconds
                            + " seconds");
        }

        // Catch the Exception
        catch (ParseException e) {
            e.printStackTrace();
        }
        return flag;
    }

}
