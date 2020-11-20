package tm.asset.api

import cn.edu.bnuz.bell.asset.stateMachine.Status
import grails.converters.JSON

class BootStrap {

    def init = { servletContext ->
        JSON.registerObjectMarshaller(Status) {
            it.name()
        }
    }
    def destroy = {
    }
}
