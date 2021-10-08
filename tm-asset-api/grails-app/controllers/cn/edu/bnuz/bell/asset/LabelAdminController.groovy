package cn.edu.bnuz.bell.asset

import cn.edu.bnuz.bell.security.SecurityService
import org.springframework.security.access.prepost.PreAuthorize


@PreAuthorize('hasAuthority("PERM_ASSET_LABEL_ADMIN")')
class LabelAdminController {
    SecurityService securityService
    LabelService labelService

    def index() {
        def labels = labelService.labels()
        def labelTypes = labelService.labelTypes()
        if (!securityService.hasRole('ROLE_ASSET_LABEL_ADMIN')) {
            labels = labels.grep {
                it.single == true
            }
            labelTypes = labelTypes.grep {
                it.single == true
            }
        }
        renderJson([labels: labels, labelTypes: labelTypes])
    }

    def save() {
        LabelCommand cmd = new LabelCommand()
        bindData(cmd, request.JSON)
        if (!cmd.typeId) {
            def type = labelService.createLabelType(cmd)
            cmd.typeId = type.id
        }
        def form = labelService.createLabel(cmd)
        renderJson([id: form.id])
    }
}
