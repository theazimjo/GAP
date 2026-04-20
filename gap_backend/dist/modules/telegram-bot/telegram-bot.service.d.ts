import { OnModuleInit } from '@nestjs/common';
import { ConfigService } from '@nestjs/config';
import { AuthService } from '../auth/auth.service';
export declare class TelegramBotService implements OnModuleInit {
    private configService;
    private authService;
    private readonly logger;
    private bot;
    constructor(configService: ConfigService, authService: AuthService);
    onModuleInit(): void;
    private setupHandlers;
    stop(): Promise<void>;
}
//# sourceMappingURL=telegram-bot.service.d.ts.map