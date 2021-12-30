package cn.edu.bnuz.bell.asset

import cn.edu.bnuz.bell.security.SecurityService
import cn.edu.bnuz.bell.security.User
import grails.converters.JSON
import grails.gorm.transactions.Transactional

@Transactional
class HindFieldService {
    SecurityService securityService

    def create(HindFieldCommand cmd) {
        HindField form = HindField.findByUserAndTableName(User.load(securityService.userId), cmd.tableName)
        if (!form) {
            form = new HindField(
                    user: User.load(securityService.userId),
                    tableName: cmd.tableName,
                    fields: "${cmd.fields as JSON}"
            )
        } else {
            form.setFields("${cmd.fields as JSON}")
        }
        if (!form.save()) {
            form.errors.each {
                println(it)
            }
        }
        return form
    }

    def findByTableName(String tableName) {
        def hindField = HindField.findByUserAndTableName(User.load(securityService.userId), tableName)
        return hindField?.fields ? JSON.parse(hindField.fields) : null
    }
}
