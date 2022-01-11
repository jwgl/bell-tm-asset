package cn.edu.bnuz.bell.asset

import cn.edu.bnuz.bell.asset.dv.DvHuisBooking
import grails.converters.JSON
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus

import com.aliyuncs.CommonRequest
import com.aliyuncs.CommonResponse
import com.aliyuncs.DefaultAcsClient
import com.aliyuncs.IAcsClient
import com.aliyuncs.exceptions.ClientException
import com.aliyuncs.exceptions.ServerException
import com.aliyuncs.http.MethodType
import com.aliyuncs.profile.DefaultProfile
import com.aliyuncs.profile.IClientProfile


class MessagerController {
    @Value('${bell.aliDomain}')
    String domain
    @Value('${bell.aliAccessId}')
    String accessId
    @Value('${bell.aliAccessSecret}')
    String accessSecret
    @Value('${bell.regionIdForPop}')
    String regionIdForPop
    @Value('${bell.templateCode}')
    String templateCode
    @Value('${bell.signName}')
    String signName

    def save() {
        MessageCommand cmd = new MessageCommand()
        bindData(cmd, request.JSON)
        def bookingInfo = DvHuisBooking.get(cmd.formId)
        if (cmd.source?.equals('/api/huis/users/61017/bookingTasks') && bookingInfo) {
            IClientProfile profile = DefaultProfile.getProfile(regionIdForPop, accessId, accessSecret)
            IAcsClient client = new DefaultAcsClient(profile)
            def stateMap = [
                    APPROVED: '通过',
                    REJECTED: '退回',
                    SENT_BACK: '驳回',
                    TERMINATED: '终止',
                    CLOSED: '关闭'
            ]
            CommonRequest request = new CommonRequest()
            request.setMethod(MethodType.POST)
            request.setDomain(domain)
            request.setVersion("2017-05-25")
            request.setAction("SendSms")
            request.putQueryParameter("RegionId", regionIdForPop)
            request.putQueryParameter("PhoneNumbers", bookingInfo.contactNumber)
            request.putQueryParameter("SignName", signName)
            request.putQueryParameter("TemplateCode", templateCode)
            def roomAndTimes = bookingInfo.roomAndTime.split(',')

            request.putQueryParameter("TemplateParam", "{\"no\": \"${bookingInfo.id}\",\"state\":\"${stateMap[bookingInfo.workflowState]}\",\"roomtime\":\"${roomAndTimes[0]}\",\"title\":\"${bookingInfo.subject}\"}")
            request.putQueryParameter("SmsUpExtendCode", "90999")

            try {
                CommonResponse response = client.getCommonResponse(request)
                def result = JSON.parse(response.getData())
                render([status: result?.Code] as JSON)
            } catch (ServerException e) {
                e.printStackTrace()
                render(status: HttpStatus.BAD_REQUEST)
            } catch (ClientException e) {
                e.printStackTrace()
                render(status: HttpStatus.BAD_REQUEST)
            }
        } else {
            renderOk()
        }
    }
}
