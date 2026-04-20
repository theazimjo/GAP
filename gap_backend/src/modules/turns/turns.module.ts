import { Module } from '@nestjs/common';
import { TypeOrmModule } from '@nestjs/typeorm';
import { Turn } from '../../entities/turn.entity';
import { Membership } from '../../entities/membership.entity';
import { TurnsService } from './turns.service';
import { TurnsController } from './turns.controller';

@Module({
  imports: [TypeOrmModule.forFeature([Turn, Membership])],
  controllers: [TurnsController],
  providers: [TurnsService],
  exports: [TurnsService],
})
export class TurnsModule {}
