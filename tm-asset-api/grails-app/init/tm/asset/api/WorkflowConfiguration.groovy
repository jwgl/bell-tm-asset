package tm.asset.api

import cn.edu.bnuz.bell.asset.AssetReviewerService
import cn.edu.bnuz.bell.workflow.DomainStateMachineHandler
import cn.edu.bnuz.bell.workflow.Event
import cn.edu.bnuz.bell.workflow.State
import cn.edu.bnuz.bell.workflow.StateObject
import cn.edu.bnuz.bell.workflow.config.DefaultStateMachineConfiguration
import cn.edu.bnuz.bell.workflow.config.DefaultStateMachinePersistConfiguration
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.statemachine.StateMachine
import org.springframework.statemachine.persist.StateMachinePersister

@Configuration
@Import([DefaultStateMachineConfiguration, DefaultStateMachinePersistConfiguration])
class WorkflowConfiguration {
    @Bean('receiptReviewStateMachine')
    DomainStateMachineHandler InfoChangeReviewStateMachine(
            @Qualifier('receiptStateMachine')
                    StateMachine<State, Event> stateMachine,
            StateMachinePersister<State, Event, StateObject> persister,
            AssetReviewerService assetReviewerService) {
        new DomainStateMachineHandler(stateMachine, persister, assetReviewerService)
    }
}
