package cn.edu.bnuz.bell.asset.stateMachine

import cn.edu.bnuz.bell.orm.PostgreSQLEnumUserType
import groovy.transform.CompileStatic

@CompileStatic
class StatusUserType extends PostgreSQLEnumUserType {
    @Override
    Class<? extends Enum> getEnumClass() {
        Status
    }
}
