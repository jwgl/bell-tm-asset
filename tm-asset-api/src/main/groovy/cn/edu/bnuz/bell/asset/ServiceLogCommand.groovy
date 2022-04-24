package cn.edu.bnuz.bell.asset

class ServiceLogCommand {
    String building
    String roomName
    String logDate
    Integer section
    String departmentId
    String contact
    String type
    String item
    String dateFinished
    String note
    String getRoom() {
        return roomName && building ? roomName.replaceAll(building, '') : ''
    }
}
