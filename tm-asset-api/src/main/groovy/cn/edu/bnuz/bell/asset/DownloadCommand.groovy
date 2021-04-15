package cn.edu.bnuz.bell.asset

import grails.validation.Validateable

class DownloadCommand implements Validateable {
    String type
    String ids
    String idList
    String cartName

}
