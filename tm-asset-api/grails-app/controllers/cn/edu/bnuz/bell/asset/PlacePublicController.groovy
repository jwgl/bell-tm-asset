package cn.edu.bnuz.bell.asset

import cn.edu.bnuz.bell.security.SecurityService
import org.springframework.security.access.prepost.PreAuthorize


@PreAuthorize('hasAuthority("PERM_ASSET_PLACE_PUBLIC")')
class PlacePublicController {
    PlaceService placeService
    PlacePublicService placePublicService
    LabelService labelService
    SecurityService securityService
    PlanService planService

    def index(RoomPublicOptionCommand cmd) {
        if (cmd.forPlan) {
         renderJson(planService.list(null))
        } else {
            renderJson([
                    rooms: placePublicService.list(cmd),
                    buildings: placeService.buildings,
                    places: placeService.places,
                    departments: Dept.findAll("from Dept order by name"),
                    labels: labelService.labels(),
                    labelTypes: labelService.labelTypes(),
                    terms: placePublicService.terms,
                    placeTypes: RoomType.executeQuery("select distinct new map(t.level1 as name) from RoomType t order by t.level1")
            ])
        }
    }

    def show(Long id, Boolean forPlan) {
        if (forPlan) {
            renderJson ([plan: planService.getFormInfo(id), labels: labelService.labels()])
        } else {
            renderJson([form: placeService.getFormInfo(id)])
        }
    }
}
