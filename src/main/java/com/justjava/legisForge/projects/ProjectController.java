package com.justjava.legisForge.projects;

import com.justjava.legisForge.aau.AuthenticationManager;
import jakarta.servlet.http.HttpServletRequest;
import org.flowable.engine.HistoryService;
import org.flowable.engine.RuntimeService;
import org.flowable.engine.TaskService;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.flowable.task.api.history.HistoricTaskInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Map;

@Controller
public class ProjectController {
    @Autowired
    private TaskService taskService;
    @Autowired
    AuthenticationManager authenticationManager;
    @Autowired
    RuntimeService runtimeService;

    @Autowired
    HistoryService historyService;

    @GetMapping("/projects")
    public String getProjects(Model model, @RequestParam Map<String, Object> startVariables) {

        String businessKey = String.valueOf(authenticationManager.get("sub"));

        // ACTIVE PROCESS INSTANCES WITH VARIABLES
        List<ProcessInstance> projects = runtimeService
                .createProcessInstanceQuery()
                .processDefinitionKey("mergerAndAquisition")
                .includeProcessVariables()
                .active()
                .list();

        System.out.println("The Total Number of Active Process Instances === " + projects.size());

        // PRINT ACTIVE PROCESS VARIABLES
        projects.forEach(proc -> {
            System.out.println("\nACTIVE Process Instance ID: " + proc.getId());

            proc.getProcessVariables().forEach((key, value) -> {
                System.out.println("   Variable Name: " + key + " | Value: " + value);
            });
        });

        // COMPLETED PROCESSES
        List<HistoricProcessInstance> completedProcess = historyService
                .createHistoricProcessInstanceQuery()
                .finished()
                .orderByProcessInstanceEndTime()
                .desc()
                .list();

        // PRINT COMPLETED PROCESS VARIABLES
        completedProcess.forEach(historic -> {
            System.out.println("\nCompleted Process Instance ID: " + historic.getId());

            historic.getProcessVariables().forEach((key, value) -> {
                System.out.println(" Variable Name: " + key + " | Value: " + value);
            });
        });

        // ADD TO MODEL
        model.addAttribute("projects", projects);
        model.addAttribute("completedP", completedProcess);
        model.addAttribute("completedProject", completedProcess.size());
        model.addAttribute("activeProject", projects.size());

        return "projects/allProjects";
    }

    @GetMapping("/project-details/{projectId}")
    public String getProjectInfo(@PathVariable String projectId, Model model) {
        ProcessInstance project=runtimeService
                .createProcessInstanceQuery()
                .processInstanceId(projectId)
                .includeProcessVariables()
                .singleResult();
        List<Task> tasks = taskService
                .createTaskQuery()
                .processInstanceId(projectId)
                .includeProcessVariables()
                .orderByTaskCreateTime().desc()
                .list();
        List<HistoricTaskInstance> historicTasks = historyService
                .createHistoricTaskInstanceQuery()
                .processInstanceId(projectId)
                .finished()
                .orderByTaskCreateTime()
                .desc()
                .listPage(0, 100);

        model.addAttribute("projectId", projectId);
        model.addAttribute("project",project);
        model.addAttribute("tasks", tasks);
        model.addAttribute("completedTasks", historicTasks);
        model.addAttribute("completedSize", historicTasks.size());
        return "projects/project-info";
    }
    @PostMapping("/create-project")
    public String startProject(
            @RequestParam Map<String, Object> startVariables,
            Model model,
            HttpServletRequest request,
            RedirectAttributes redirectAttributes) {  // Add this for flash messages (optional but nice)

        // === PREVENT DUPLICATE EXECUTION (the unwanted replay after redirect) ===
        // If no meaningful field is present → it's the bogus replay
        if (!startVariables.containsKey("transactionType") ||
                startVariables.get("transactionType") == null ||
                startVariables.get("transactionType").toString().trim().isEmpty()) {

            String referer = request.getHeader("Referer");
            return "redirect:" + (referer != null ? referer : "/");
        }
        // === Real request starts here ===
        System.out.println("The Sent Parameter Here === " + startVariables);

        String businessKey = String.valueOf(authenticationManager.get("sub"));
        startVariables.put("progress", 0);

        ProcessInstance processInstance = runtimeService
                .startProcessInstanceByKey("mergerAndAquisition", businessKey, startVariables);

        System.out.println("Process started successfully with ID: "
                + processInstance.getProcessInstanceId()
                + " and BusinessKey: " + processInstance.getBusinessKey());

        // Refresh active projects
        List<ProcessInstance> projects = runtimeService
                .createProcessInstanceQuery()
                .processDefinitionKey("mergerAndAquisition")
                .includeProcessVariables()
                .active()
                .list();

        // Completed count
        long completedCount = historyService
                .createHistoricProcessInstanceQuery()
                .finished()
                .count();  // Faster than list() + size()

        // Add everything to model (only on real submission)
        model.addAttribute("projects", projects);
        model.addAttribute("completedProject", completedCount);
        model.addAttribute("activeProject", projects.size());

        // Optional: nice success message
        redirectAttributes.addFlashAttribute("successMessage",
                "Project created successfully!");

        // Redirect back to the same page (PRG pattern)
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
