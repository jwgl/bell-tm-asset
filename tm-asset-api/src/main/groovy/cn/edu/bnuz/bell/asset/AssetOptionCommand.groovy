package cn.edu.bnuz.bell.asset

import grails.validation.Validateable

class AssetOptionCommand implements Validateable{
    List<String> assetNames
    List<String> buildings
    List<String> places
    String state

    Map getArgs() {
        def arg = [:]
        if (testList(buildings)) {
            arg += [buildings: buildings]
        }
        if (testList(places)) {
            arg += [places: places]
        }
        if (testList(assetNames)) {
            arg += [assetNames: assetNames]
        }
        if (state) {
            arg += [state: state]
        }
        return arg
    }

    String getCriterion() {
        def criterion = ''

        if (testList(buildings)) {
            criterion += "${criterion.isEmpty() ? "" : " and "}r.building in (:buildings)"
        }
        if (testList(places)) {
            criterion += "${criterion.isEmpty() ? "" : " and "}r.name in (:places)"
        }
        if (testList(assetNames)) {
            criterion += "${criterion.isEmpty() ? "" : " and "}a.name in (:assetNames)"
        }
        if (state) {
            criterion += "${criterion.isEmpty() ? "" : " and "}a.state = :state"
        }

        return criterion
    }

    boolean testList(List<String> object) {
        return object?.size() > 0 && object != ['null']
    }
}
