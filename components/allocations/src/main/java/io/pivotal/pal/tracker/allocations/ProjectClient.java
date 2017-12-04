package io.pivotal.pal.tracker.allocations;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestOperations;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;

public class ProjectClient {

    private final RestOperations restOperations;
    private final String registrationServerEndpoint;
    private final Map<Long, ProjectInfo> projectsCache = new ConcurrentHashMap<>();
    private final Logger logger = LoggerFactory.getLogger(getClass());

    public ProjectClient(RestOperations restOperations, String registrationServerEndpoint) {
        this.restOperations= restOperations;
        this.registrationServerEndpoint = registrationServerEndpoint;
    }

    @HystrixCommand(fallbackMethod = "getProjectFromCache")
    public ProjectInfo getProject(long projectId) {
        ProjectInfo project  = restOperations.getForObject(registrationServerEndpoint + "/projects/" + projectId, ProjectInfo.class);
        projectsCache.put(projectId, project);

        return project;
    }

    public ProjectInfo getProjectFromCache(long projectId) {
        logger.info( "Getting project with id {} from Cache", projectId);
        return projectsCache.get(projectId);

    }
}
