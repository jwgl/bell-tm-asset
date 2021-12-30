package cn.edu.bnuz.bell.asset

import org.springframework.security.access.prepost.PreAuthorize


@PreAuthorize('hasRole("ROLE_IN_SCHOOL_TEACHER")')
class HindFieldController {
    HindFieldService hindFieldService
    def save() {
        HindFieldCommand cmd = new HindFieldCommand()
        bindData(cmd, request.JSON)
        def form = hindFieldService.create(cmd)
        renderJson([id: form.id])
    }
}
