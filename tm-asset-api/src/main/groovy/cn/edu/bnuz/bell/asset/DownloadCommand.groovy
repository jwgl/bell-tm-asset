package cn.edu.bnuz.bell.asset

import grails.validation.Validateable

class DownloadCommand implements Validateable {
    String type
    List<Long> ids
    private String idList

    void setIdList(String value) {
        this.idList = value
        if (value) {
            this.ids = []
            def idsText = value.split(';')
            idsText.each {
                this.ids += it.toLong()
            }
        }
    }
}
