package cn.edu.bnuz.bell.asset

import grails.validation.Validateable

import java.time.LocalDate

class PlaceUsageCommand implements Validateable {
    LocalDate date
    Integer section
    String room
}
