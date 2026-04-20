import { Controller, Get, Post, Body, Param, UseGuards, Req } from '@nestjs/common';
import { PaymentsService } from './payments.service';
import { JwtAuthGuard } from '../auth/guards/jwt-auth.guard';

@Controller('api')
@UseGuards(JwtAuthGuard)
export class PaymentsController {
  constructor(private readonly paymentsService: PaymentsService) {}

  @Post('pay')
  async pay(@Body() body: any, @Req() req: any) {
    return this.paymentsService.markPayment(
      body.turnId,
      body.amount,
      body.status,
      req.user.userId,
    );
  }

  @Get('payments/:turnId')
  async getPayments(@Param('turnId') turnId: string) {
    return this.paymentsService.getTurnPayments(+turnId);
  }
}
