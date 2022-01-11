package cn.edu.bnuz.bell.asset

import cn.edu.bnuz.bell.asset.dv.DvHuisBooking
import grails.gorm.transactions.Transactional

@Transactional
class MessagerService {

    def getBookingInfo(Long id) {
        DvHuisBooking.get(id)
    }
}
