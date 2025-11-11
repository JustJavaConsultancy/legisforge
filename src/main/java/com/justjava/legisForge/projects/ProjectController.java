package com.justjava.legisForge.projects;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ProjectController {
    @GetMapping("/projects")
    public String getProjects() {
        return "projects/allProjects";
    }
    @GetMapping("/project-info")
    public String getProjectInfo() {
        return "projects/project-info";
    }
}
