package cn.edu.bnuz.bell.asset

import grails.validation.Validateable

class RoomPublicOptionCommand implements Validateable {
    List<String> buildings
    List<String> roomNames
    List<String> departments
    List<String> groups
    Integer measureL
    Integer measureH
    Integer seatL
    Integer seatH
    Integer termId
    Boolean forPlan
    List<Long> labels
    Boolean isReform

    Map getArgs() {
        def arg = [:]

        if (buildings?.size()) {
            arg += [buildings: buildings]
        }
        if (roomNames?.size()) {
            arg += [roomNames: roomNames]
        }
        if (departments?.size()) {
            arg += [departments: departments]
        }
        if (groups?.size()) {
            arg += [groups: groups]
        }
        if (measureH > 0 && measureH > measureL) {
            arg += [measureL: measureL, measureH: measureH]
        }
        if (seatH > 0 && seatH > seatL) {
            arg += [seatL: seatL, seatH: seatH]
        }
        if (forPlan) {
            arg += [termId: termId]
        }
        if (labels) {
            arg += [labels: labels]
        }
        return arg
    }

    String getCriterion() {
        def criterion = ''

        if (buildings?.size()) {
            criterion += "and dr.building in (:buildings)"
        }
        if (roomNames?.size()) {
            criterion += " and dr.name in (:roomNames)"
        }
        if (departments?.size()) {
            criterion += " and dr.department in (:departments)"
        }
        if (groups?.size()) {
            criterion += " and dr.groups in (:groups)"
        }
        if (measureH > 0 && measureH > measureL) {
            criterion += " and dr.measure between :measureL and :measureH"
        }
        if (seatH > 0 && seatH > seatL) {
            criterion += " and dr.seat between :seatL and :seatH"
        }
        if (isReform) {
            criterion += " and dr.isReform is true"
        }

        return criterion
    }
}
