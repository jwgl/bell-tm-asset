package cn.edu.bnuz.bell.asset.stateMachine

import groovy.transform.CompileStatic

@CompileStatic
enum Event {
    UPDATE,
    CHECKIN,
    STOP,
    FIX,
    ALLOT,
    CHECKOUT,
    REPAIR,
    LOSE,
    FORBID,
    TRANSFER,
    CLOSE,
}
