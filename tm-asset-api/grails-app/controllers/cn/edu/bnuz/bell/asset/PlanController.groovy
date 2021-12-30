package cn.edu.bnuz.bell.asset

import cn.edu.bnuz.bell.http.ForbiddenException
import cn.edu.bnuz.bell.workflow.Event
import grails.converters.JSON
import org.springframework.security.access.prepost.PreAuthorize

@PreAuthorize('hasAuthority("PERM_ASSET_PLACE_WRITE")')
class PlanController {
    PlanService planService
    LabelService labelService
    def index(Integer termId) {
        renderJson(planService.list(termId))
    }

    def show(Long id) {
        renderJson ([plan: planService.getFormInfo(id), labels: labelService.labels()])
    }

    def edit(Long id) {
        renderJson planService.getFormForEdit(id)
    }

    def update(Long id) {
        PlanCommand cmd = new PlanCommand()
        bindData(cmd, request.JSON)
        planService.create(cmd)
        renderOk()
    }

    def patch(Long id, String op) {
        def operation = Event.valueOf(op)
        Plan plan = Plan.load(id)
        if (!plan) {
            throw new ForbiddenException()
        }
        switch (operation) {
            case Event.FINISH:
                excutePlan(plan)
                planService.executePlan(plan, 'FINISHED')
                break
            case Event.PROCESS:
                excutePlan(plan)
                planService.executePlan(plan, 'DOING')
                break
            case Event.CANCEL:
                planService.executePlan(plan, 'CANCEL')
                break
            default:
                throw new ForbiddenException()
        }
        renderOk()
    }

    private excutePlan(Plan plan) {
        def rooms = JSON.parse(plan.info)
        if (rooms) {
            rooms.each {
                RoomCommand cmd = new RoomCommand()
                bindData(cmd, it)
                planService.createRoom(cmd)
            }
        }
    }
}
