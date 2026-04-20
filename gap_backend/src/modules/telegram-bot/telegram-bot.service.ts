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
        
        // Check if user still has a placeholder phone (e.g. starts with tg_)
        const hasRealPhone = result.user.phone && !result.user.phone.startsWith('tg_');

        // Create Redirect Link for App (to bypass Telegram protocol restriction)
        const loginUrl = `http://104.248.43.194:2030/api/auth/callback?token=${result.token}`;

        if (!hasRealPhone) {
          await ctx.reply(
            `Salom ${first_name}! GAP ilovasiga xush kelibsiz. Tizimdan to'liq foydalanish uchun telefon raqamingizni tasdiqlang.`,
            Markup.keyboard([
              Markup.button.contactRequest('📞 Telefon raqamni yuborish'),
            ]).resize().oneTime(),
          );
        } else {
          await ctx.reply(
            `Salom ${first_name}! GAP ilovasiga xush kelibsiz.`,
            Markup.inlineKeyboard([
              Markup.button.url('Ilovaga qaytish (Kirish)', loginUrl),
            ]),
          );
        }
      } catch (error) {
        this.logger.error('Error during bot login', error);
        await ctx.reply('Kechirasiz, tizimga kirishda xatolik yuz berdi.');
      }
    });

    // Handle Contact Sharing
    this.bot.on('contact', async (ctx) => {
      const { phone_number, user_id } = ctx.message.contact;
      const telegramId = user_id.toString();

      try {
        this.logger.log(`User ${telegramId} shared contact: ${phone_number}`);
        
        // Clean phone number (remove +, spaces, etc)
        const cleanPhone = phone_number.replace(/\+/g, '');
        
        // Find and update user
        const user = await this.authService.updatePhoneByTelegramId(telegramId, cleanPhone);
        
        // Log back in to get new token
        const result = await this.authService.loginByTelegramId(parseInt(telegramId), user.name);
        const loginUrl = `http://104.248.43.194:2030/api/auth/callback?token=${result.token}`;

        await ctx.reply(
          `Rahmat! Telefon raqamingiz (${phone_number}) muvaffaqiyatli tasdiqlandi.`,
          Markup.removeKeyboard(),
        );

        await ctx.reply(
          'Endi ilovaga kirishingiz mumkin:',
          Markup.inlineKeyboard([
            Markup.button.url('Ilovaga qaytish (Kirish)', loginUrl),
          ]),
        );
      } catch (error) {
        this.logger.error('Error handling contact sharing', error);
        await ctx.reply('Kechirasiz, kontaktni saqlashda xatolik yuz berdi.');
      }
    });

    this.bot.help((ctx) => ctx.reply('Ilovaga kirish uchun /start ni bosing.'));
  }

  async stop() {
    await this.bot.stop();
  }
}
