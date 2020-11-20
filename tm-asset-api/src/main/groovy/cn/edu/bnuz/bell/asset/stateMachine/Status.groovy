package cn.edu.bnuz.bell.asset.stateMachine

import groovy.transform.CompileStatic

@CompileStatic
enum Status {
    USING,
    STANDBY,
    REPAIRING,
    OFF,
    CLEARANCE,
    LOST,
}