package cn.edu.bnuz.bell.asset

import grails.validation.Validateable

class PlaceUsageCommand implements Validateable {
    String logDate
    Integer section
    String roomName
    String building

    String getName() {
        return roomName && building ? roomName.replaceAll(building, '') : ''
    }
}
