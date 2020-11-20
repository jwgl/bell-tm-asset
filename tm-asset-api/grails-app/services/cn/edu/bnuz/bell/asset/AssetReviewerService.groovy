package cn.edu.bnuz.bell.asset

import cn.edu.bnuz.bell.http.BadRequestException
import cn.edu.bnuz.bell.security.User
import cn.edu.bnuz.bell.workflow.Activities
import cn.edu.bnuz.bell.workflow.ReviewerProvider
import grails.gorm.transactions.Transactional

@Transactional(readOnly = true)
class AssetReviewerService implements ReviewerProvider{

    List<Map> getReviewers(Object id, String activity) {
        switch (activity) {
            case Activities.CHECK:
                return getCheckers(id) + getApprovers()
            case Activities.APPROVE:
                return getApprovers()
            case Activities.REVIEW:
                return getReviewers() + getApprovers()
            default:
                throw new BadRequestException()
        }
    }

    List<Map> getCheckers() {
        User.findAllWithPermission('PERM_ASSET_ADVICE_WRITE')
    }

    List<Map> getApprovers() {
        User.findAllWithPermission('PERM_ASSET_APPROVAL')
    }
}
