package tm.asset.api

import cn.edu.bnuz.bell.workflow.Activities
import cn.edu.bnuz.bell.workflow.Event
import cn.edu.bnuz.bell.workflow.State
import cn.edu.bnuz.bell.workflow.actions.AutoEntryAction
import cn.edu.bnuz.bell.workflow.actions.SubmittedEntryAction
import cn.edu.bnuz.bell.workflow.config.StandardActionConfiguration
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import
import org.springframework.statemachine.action.Action
import org.springframework.statemachine.config.EnableStateMachine
import org.springframework.statemachine.config.EnumStateMachineConfigurerAdapter
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer

@Configuration
@EnableStateMachine(name='receiptStateMachine')
@Import(StandardActionConfiguration)
class ReceiptStateMachineConfiguration extends EnumStateMachineConfigurerAdapter<State, Event> {
    @Autowired
    StandardActionConfiguration actions

    @Override
    void configure(StateMachineStateConfigurer<State, Event> states) throws Exception {
        states
                .withStates()
                .initial(State.CREATED)
                .state(State.CREATED,   [actions.logEntryAction()], null)
                .state(State.SUBMITTED, [actions.logEntryAction(), submittedEntryAction()],  [actions.workitemProcessedAction()])
                .state(State.APPROVED,  [actions.logEntryAction(), approvedEntryAction()], null)
                .state(State.REJECTED,  [actions.logEntryAction(), actions.rejectedEntryAction()],  [actions.workitemProcessedAction()])
                .state(State.FINISHED,  [actions.logEntryAction(), actions.notifySubmitterAction()], null)
    }

    @Override
    void configure(StateMachineTransitionConfigurer<State, Event> transitions) throws Exception {
        transitions
                .withInternal()
                .source(State.CREATED)
                .event(Event.UPDATE)
                .action(actions.logTransitionAction())
                .and()
                .withExternal()
                .source(State.CREATED)
                .event(Event.SUBMIT)
                .target(State.SUBMITTED)
                .and()
                .withInternal()
                .source(State.REJECTED)
                .event(Event.UPDATE)
                .action(actions.logTransitionAction())
                .and()
                .withExternal()
                .source(State.REJECTED)
                .event(Event.SUBMIT)
                .target(State.SUBMITTED)
                .and()
                .withExternal()
                .source(State.SUBMITTED)
                .event(Event.ACCEPT)
                .target(State.APPROVED)
                .and()
                .withExternal()
                .source(State.APPROVED)
                .event(Event.ACCEPT)
                .target(State.PROGRESS)
                .and()
                .withExternal()
                .source(State.PROGRESS)
                .event(Event.ACCEPT)
                .target(State.FINISHED)
                .and()
                .withExternal()
                .source(State.SUBMITTED)
                .event(Event.REJECT)
                .target(State.REJECTED)
                .and()
                .withExternal()
                .source(State.SUBMITTED)
                .event(Event.CLOSE)
                .target(State.CLOSED)
    }

    @Bean
    Action<State, Event> approvedEntryAction() {
        new AutoEntryAction()
    }

    @Bean
    Action<State, Event> submittedEntryAction() {
        new SubmittedEntryAction(Activities.APPROVE)
    }

}
