package io.pivotal.pal.tracker.backlog;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.client.RestOperations;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.netflix.hystrix.contrib.javanica.annotation.HystrixCommand;

public class ProjectClient {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final Map<Long, ProjectInfo> projectsCache = new ConcurrentHashMap<>();
    private final RestOperations restOperations;
    private final String endpoint;

    public ProjectClient(RestOperations restOperations, String registrationServerEndpoint) {
        this.restOperations = restOperations;
        this.endpoint = registrationServerEndpoint;
    }

    @HystrixCommand(fallbackMethod = "getProjectFromCache")
    public ProjectInfo getProject(long projectId) {

        ProjectInfo prj = restOperations.getForObject(endpoint + "/projects/" + projectId, ProjectInfo.class);
        projectsCache.put(projectId, prj);

        return prj;
    }

    public ProjectInfo getProjectFromCache(long projectId){
        logger.info("Getting project with id {} from cache", projectId);
        return projectsCache.get(projectId);
    }
}
