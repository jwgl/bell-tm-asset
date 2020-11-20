package cn.edu.bnuz.bell.asset

import grails.validation.Validateable

class RoomOptionCommand implements Validateable{
    String name
    List<Long> seatTypeIds
    List<String> buildings
    Integer seat
    String status
    List<Long> placeTypeIds
    List<Long> departmentIds

    Map getArgs() {
        def arg = [:]

        if (seatTypeIds?.size() > 0) {
            arg += [seatTypeIds: seatTypeIds]
        }
        if (buildings?.size()) {
            arg += [buildings: buildings]
        }
        if (placeTypeIds?.size()) {
            arg += [placeTypeIds: placeTypeIds]
        }
        if (departmentIds?.size()) {
            arg += [departmentIds: departmentIds]
        }
        if (name) {
            arg += [name: name]
        }
        if (seat) {
            arg += [seat: seat]
        }
        if (status) {
            arg += [status: status]
        }
        return arg
    }

    String getCriterion() {
        def criterion = ''

        if (seatTypeIds?.size() > 0) {
            criterion += "${criterion.isEmpty() ? "" : " and "}ts.id in (:seatTypeIds)"
        }
        if (buildings?.size()) {
            criterion += "${criterion.isEmpty() ? "" : " and "}r.building in (:buildings)"
        }
        if (placeTypeIds?.size()) {
            criterion += "${criterion.isEmpty() ? "" : " and "}tp.id in (:placeTypeIds)"
        }
        if (departmentIds?.size()) {
            criterion += "${criterion.isEmpty() ? "" : " and "}d.id in (:departmentIds)"
        }
        if (name) {
            criterion += "${criterion.isEmpty() ? "" : " and "}r.name = :name"
        }
        if (seat) {
            criterion += "${criterion.isEmpty() ? "" : " and "}r.seat = :seat"
        }
        if (status) {
            criterion += "${criterion.isEmpty() ? "" : " and "}r.status = :status"
        }

        return criterion
    }
}
