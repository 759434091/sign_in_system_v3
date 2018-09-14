package team.a9043.sign_in_system.controller;

import org.json.JSONObject;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import team.a9043.sign_in_system.exception.IncorrectParameterException;
import team.a9043.sign_in_system.pojo.SisLocation;
import team.a9043.sign_in_system.service.LocationService;

import javax.annotation.Resource;

/**
 * @author a9043
 */
@RestController
public class LocationController {
    @Resource
    private LocationService locationService;

    @PutMapping("/schedules/{ssId}/locations/{slId}")
    @PreAuthorize("hasAnyAuthority('TEACHER','ADMINISTRATOR')")
    public JSONObject modifyScheduleLocation(@PathVariable Integer ssId,
                                             @PathVariable Integer slId) throws IncorrectParameterException {
        return locationService.modifyScheduleLocation(ssId, slId);
    }

    @GetMapping("/locations/{slId}")
    public JSONObject getLocation(@PathVariable Integer slId) throws IncorrectParameterException {
        return locationService.getLocation(slId);
    }

    @GetMapping("/locations")
    public JSONObject getLocations(@RequestParam Integer page,
                                   @RequestParam Integer pageSize,
                                   @RequestParam(required = false) Integer slId,
                                   @RequestParam(required = false) String slName) throws IncorrectParameterException {
        return locationService.getLocations(page, pageSize, slId, slName);
    }

    @PutMapping("/locations/{slId}")
    @PreAuthorize("hasAuthority('ADMINISTRATOR')")
    public JSONObject modifyLocation(@PathVariable Integer slId,
                                     @RequestBody SisLocation sisLocation) throws IncorrectParameterException {
        return locationService.modifyLocation(slId, sisLocation);
    }

    @DeleteMapping("/locations/{slId}")
    @PreAuthorize("hasAuthority('ADMINISTRATOR')")
    public JSONObject deleteLocation(@PathVariable Integer slId) throws IncorrectParameterException {
        return locationService.deleteLocation(slId);
    }

    @PostMapping("/locations")
    @PreAuthorize("hasAuthority('ADMINISTRATOR')")
    public JSONObject createLocation(@RequestBody SisLocation sisLocation) {
        return locationService.createLocation(sisLocation);
    }
}
