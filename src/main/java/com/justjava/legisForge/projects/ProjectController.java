package com.justjava.legisForge.projects;

import com.justjava.legisForge.aau.AuthenticationManager;
import jakarta.servlet.http.HttpServletRequest;
import org.flowable.engine.HistoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.engine.runtime.ProcessInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

@Controller
public class ProjectController {
    @Autowired
    AuthenticationManager authenticationManager;
    @Autowired
    RuntimeService runtimeService;

    @Autowired
    HistoryService historyService;

    @GetMapping("/projects")
    public String getProjects() {
        return "projects/allProjects";
    }
    @GetMapping("/project-info")
    public String getProjectInfo() {
        return "projects/project-info";
    }
    @PostMapping("/create-project")
        public String startProject(@RequestParam Map<String,Object> startVariables, Model model, HttpServletRequest
        request) {

        System.out.println(" The Sent Parameter Here === " + startVariables);

        String businessKey = String.valueOf(authenticationManager.get("sub"));
        startVariables.put("progress", 0);

        ProcessInstance processInstance = runtimeService
                .startProcessInstanceByKey("mergerAndAquisition", businessKey, startVariables);


        System.out.println("Process started successfully with ID: "
                + processInstance.getProcessInstanceId()
                + " and BusinessKey: " + processInstance.getBusinessKey());

        List<ProcessInstance> projects = runtimeService
                .createProcessInstanceQuery()
                .processDefinitionKey("mergerAndAquisition")
                .includeProcessVariables()
                .active()
                .list();

        System.out.println(" The Total Number of Active Process Instances ===" + projects.size());
        List<HistoricProcessInstance> completedProcess = historyService
                .createHistoricProcessInstanceQuery()
                .finished()
                .orderByProcessInstanceEndTime()
                .desc()
                .list();

        model.addAttribute("projects", projects);
        model.addAttribute("completedProject", completedProcess.size());
        model.addAttribute("activeProject", projects.size());

        // Get the page the request came from
        String referer = request.getHeader("Referer");
        return "redirect:" + (referer != null ? referer : "/");
    }
    @GetMapping("/ipUpload")
    public String ipUpload() {
        return "tasks/ipUpload";
    }
    @GetMapping("/combined-ip")
    public String combinedIp() {
        return "tasks/combinedIp";
    }
    @GetMapping("/ip-risk")
    public String ipRisk() {
        return "tasks/ipRisk";
    }
    @GetMapping("/employment-upload")
    public String employmentUpload() {
        return "tasks/employmentUpload";
    }
    @GetMapping("/combined-employment")
    public String combinedEmployment() {
        return "tasks/combinedEmployment";
    }
    @GetMapping("/employment-risk")
    public String employmentRisk() {
        return "tasks/employmentRisk";
    }
    @GetMapping("/corporate-upload")
    public String corporateUpload() {
        return "tasks/corporateUpload";
    }
    @GetMapping("/combined-corporate")
    public String combinedCorporate() {
        return "tasks/combinedCorporate";
    }
    @GetMapping("/corporate-risk")
    public String corporateRisk() {
        return "tasks/corporateRisk";
    }
    @GetMapping("/combined-report")
    public String combinedReport() {
        return "tasks/combinedRisk";
    }
    @GetMapping("/red-document")
    public String redDocument() {
        return "tasks/redDocument";
    }
    @GetMapping("/upload-company-documents")
    public String uploadCompanyDocuments() {
        return "tasks/uploadCompanyDocuments";
    }
}
