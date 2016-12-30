package by.rudkouski.auction.controller;

import by.rudkouski.auction.command.*;
import by.rudkouski.auction.command.impl.*;

public enum CommandType {
    LOCALE(new LocaleCommand()),
    SETUP_CATEGORY(new SetupCategoryCommand()),
    SETUP_LOT(new SetupLotCommand()),
    CATEGORY_CHOICE(new CategoryChoiceCommand()),
    LOT_CHOICE(new LotChoiceCommand()),
    LOGIN(new LogInCommand()),
    REGISTER(new RegisterCommand()),
    LOGOUT(new LogOutCommand()),
    LOT_SEARCH(new LotSearchCommand()),
    PAGE_NEXT(new PageNextCommand()),
    PAGE_BACK(new PageBackCommand()),
    BET_ADD(new BetAddCommand()),
    PROFILE(new ProfileCommand()),
    LOT_HISTORY(new LotHistoryCommand()),
    BET_HISTORY(new BetHistoryCommand()),
    PROFILE_CHANGE(new ProfileChangeCommand()),
    BALANCE_FILL(new BalanceFillCommand()),
    LOT_NEW(new LotNewCommand()),
    LOT_SAVE(new LotSaveCommand());

    ICommand command;

    CommandType(ICommand command) {
        this.command = command;
    }

    public ICommand getCurrentCommand() {
        return command;
    }
}