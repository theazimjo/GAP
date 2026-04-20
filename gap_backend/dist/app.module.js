"use strict";
var __decorate = (this && this.__decorate) || function (decorators, target, key, desc) {
    var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
    if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
    else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
    return c > 3 && r && Object.defineProperty(target, key, r), r;
};
Object.defineProperty(exports, "__esModule", { value: true });
exports.AppModule = void 0;
const common_1 = require("@nestjs/common");
const typeorm_1 = require("@nestjs/typeorm");
const config_1 = require("@nestjs/config");
const auth_module_1 = require("./modules/auth/auth.module");
const users_module_1 = require("./modules/users/users.module");
const groups_module_1 = require("./modules/groups/groups.module");
const turns_module_1 = require("./modules/turns/turns.module");
const payments_module_1 = require("./modules/payments/payments.module");
const common_module_1 = require("./modules/common/common.module");
const telegram_bot_module_1 = require("./modules/telegram-bot/telegram-bot.module");
// Entities
const user_entity_1 = require("./entities/user.entity");
const group_entity_1 = require("./entities/group.entity");
const membership_entity_1 = require("./entities/membership.entity");
const turn_entity_1 = require("./entities/turn.entity");
const payment_entity_1 = require("./entities/payment.entity");
let AppModule = class AppModule {
};
exports.AppModule = AppModule;
exports.AppModule = AppModule = __decorate([
    (0, common_1.Module)({
        imports: [
            config_1.ConfigModule.forRoot({ isGlobal: true }),
            typeorm_1.TypeOrmModule.forRoot({
                type: 'postgres',
                url: process.env.DATABASE_URL,
                entities: [user_entity_1.User, group_entity_1.Group, membership_entity_1.Membership, turn_entity_1.Turn, payment_entity_1.Payment],
                synchronize: true, // Should be false in production
            }),
            auth_module_1.AuthModule,
            users_module_1.UsersModule,
            groups_module_1.GroupsModule,
            turns_module_1.TurnsModule,
            payments_module_1.PaymentsModule,
            common_module_1.CommonModule,
            telegram_bot_module_1.TelegramBotModule,
        ],
    })
], AppModule);
//# sourceMappingURL=app.module.js.map