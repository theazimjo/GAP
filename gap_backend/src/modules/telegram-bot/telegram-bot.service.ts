import { Injectable, OnModuleInit, Logger } from '@nestjs/common';
import { ConfigService } from '@nestjs/config';
import { Telegraf, Markup } from 'telegraf';
import { AuthService } from '../auth/auth.service';

@Injectable()
export class TelegramBotService implements OnModuleInit {
  private readonly logger = new Logger(TelegramBotService.name);
  private bot: Telegraf;

  constructor(
    private configService: ConfigService,
    private authService: AuthService,
  ) {
    const token = this.configService.get<string>('TELEGRAM_BOT_TOKEN');
    if (!token) {
      throw new Error('TELEGRAM_BOT_TOKEN not found in environment');
    }
    this.bot = new Telegraf(token);
  }

  onModuleInit() {
    this.setupHandlers();
    this.logger.log('Telegram Bot initialized');
    // Start bot in background
    this.bot.launch().catch((err) => {
      this.logger.error('Failed to launch Telegram Bot', err);
    });
  }

  private setupHandlers() {
    this.bot.start(async (ctx) => {
      const { id, first_name, last_name } = ctx.from;
      this.logger.log(`User ${id} started the bot`);

      try {
        // Authenticate user and get token
        const result = await this.authService.loginByTelegramId(id, first_name, last_name);
        
        // Create Redirect Link for App (to bypass Telegram protocol restriction)
        const loginUrl = `http://104.248.43.194:2030/api/auth/callback?token=${result.token}`;

        await ctx.reply(
          `Salom ${first_name}! GAP ilovasiga xush kelibsiz.`,
          Markup.inlineKeyboard([
            Markup.button.url('Ilovaga qaytish (Kirish)', loginUrl),
          ]),
        );
      } catch (error) {
        this.logger.error('Error during bot login', error);
        await ctx.reply('Kechirasiz, tizimga kirishda xatolik yuz berdi.');
      }
    });

    this.bot.help((ctx) => ctx.reply('Ilovaga kirish uchun /start ni bosing.'));
  }

  async stop() {
    await this.bot.stop();
  }
}
