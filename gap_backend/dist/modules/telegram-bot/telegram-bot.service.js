"use strict";
var __decorate = (this && this.__decorate) || function (decorators, target, key, desc) {
    var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
    if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
    else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
    return c > 3 && r && Object.defineProperty(target, key, r), r;
};
var __metadata = (this && this.__metadata) || function (k, v) {
    if (typeof Reflect === "object" && typeof Reflect.metadata === "function") return Reflect.metadata(k, v);
};
var TelegramBotService_1;
Object.defineProperty(exports, "__esModule", { value: true });
exports.TelegramBotService = void 0;
const common_1 = require("@nestjs/common");
const config_1 = require("@nestjs/config");
const telegraf_1 = require("telegraf");
const auth_service_1 = require("../auth/auth.service");
let TelegramBotService = TelegramBotService_1 = class TelegramBotService {
    configService;
    authService;
    logger = new common_1.Logger(TelegramBotService_1.name);
    bot;
    constructor(configService, authService) {
        this.configService = configService;
        this.authService = authService;
        const token = this.configService.get('TELEGRAM_BOT_TOKEN');
        if (!token) {
            throw new Error('TELEGRAM_BOT_TOKEN not found in environment');
        }
        this.bot = new telegraf_1.Telegraf(token);
    }
    onModuleInit() {
        this.setupHandlers();
        this.logger.log('Telegram Bot initialized');
        // Start bot in background
        this.bot.launch().catch((err) => {
            this.logger.error('Failed to launch Telegram Bot', err);
        });
    }
    setupHandlers() {
        this.bot.start(async (ctx) => {
            const { id, first_name, last_name } = ctx.from;
            this.logger.log(`User ${id} started the bot`);
            try {
                // Authenticate user and get token
                const result = await this.authService.loginByTelegramId(id, first_name, last_name);
                // Create Deep Link for App
                // Example: abs.uits.gap://login?token=xyz
                const loginUrl = `abs.uits.gap://login?token=${result.token}`;
                await ctx.reply(`Salom ${first_name}! GAP ilovasiga xush kelibsiz.`, telegraf_1.Markup.inlineKeyboard([
                    telegraf_1.Markup.button.url('Ilovaga qaytish (Kirish)', loginUrl),
                ]));
            }
            catch (error) {
                this.logger.error('Error during bot login', error);
                await ctx.reply('Kechirasiz, tizimga kirishda xatolik yuz berdi.');
            }
        });
        this.bot.help((ctx) => ctx.reply('Ilovaga kirish uchun /start ni bosing.'));
    }
    async stop() {
        await this.bot.stop();
    }
};
exports.TelegramBotService = TelegramBotService;
exports.TelegramBotService = TelegramBotService = TelegramBotService_1 = __decorate([
    (0, common_1.Injectable)(),
    __metadata("design:paramtypes", [config_1.ConfigService,
        auth_service_1.AuthService])
], TelegramBotService);
//# sourceMappingURL=telegram-bot.service.js.map